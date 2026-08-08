/* ============================================================
   Revelar — lógica do visualizador
   Tudo roda no navegador: a foto nunca sai do dispositivo.
   ============================================================ */

const els = {
    dropzone:  document.getElementById('dropzone'),
    fileInput: document.getElementById('fileInput'),
    viewer:    document.getElementById('viewer'),
    photo:     document.getElementById('photo'),
    back:      document.getElementById('backButton'),
    toggle:    document.getElementById('panelToggle'),
    place:     document.getElementById('metaPlace'),
    headline:  document.getElementById('metaHeadline'),
    dek:       document.getElementById('metaDek'),
    caption:   document.getElementById('metaCaption'),
    swatches:  document.getElementById('metaSwatches'),
    specs:     document.getElementById('metaSpecs'),
    mapBlock:  document.getElementById('mapBlock'),
    map:       document.getElementById('map'),
    coords:    document.getElementById('metaCoords'),
};

let mapInstance = null;
let currentObjectUrl = null;

/* ---------- Entrada de arquivo (clique + drag&drop) ---------- */
els.fileInput.addEventListener('change', (e) => {
    if (e.target.files && e.target.files[0]) loadPhoto(e.target.files[0]);
});

['dragenter', 'dragover'].forEach(ev =>
    els.dropzone.addEventListener(ev, (e) => { e.preventDefault(); els.dropzone.classList.add('is-drag'); }));
['dragleave', 'drop'].forEach(ev =>
    els.dropzone.addEventListener(ev, (e) => { e.preventDefault(); els.dropzone.classList.remove('is-drag'); }));
els.dropzone.addEventListener('drop', (e) => {
    const file = e.dataTransfer.files && e.dataTransfer.files[0];
    if (file && file.type.startsWith('image/')) loadPhoto(file);
});
// Também aceita soltar a foto direto sobre o visualizador
els.viewer.addEventListener('dragover', (e) => e.preventDefault());
els.viewer.addEventListener('drop', (e) => {
    e.preventDefault();
    const file = e.dataTransfer.files && e.dataTransfer.files[0];
    if (file && file.type.startsWith('image/')) loadPhoto(file);
});

/* ---------- Navegação de painel ---------- */
els.toggle.addEventListener('click', () => els.viewer.classList.toggle('panel-open'));
els.back.addEventListener('click', goHome);

function goHome() {
    els.viewer.hidden = true;
    els.dropzone.hidden = false;
    els.viewer.classList.remove('panel-open');
    els.fileInput.value = '';
}

/* ============================================================
   Fluxo principal
   ============================================================ */
async function loadPhoto(file) {
    if (currentObjectUrl) URL.revokeObjectURL(currentObjectUrl);
    currentObjectUrl = URL.createObjectURL(file);

    // Mostra a imagem
    els.photo.src = currentObjectUrl;
    els.dropzone.hidden = true;
    els.viewer.hidden = false;
    els.viewer.classList.remove('panel-open');

    // Zera painel
    resetPanel(file);

    // Espera a imagem carregar para extrair cores e dimensões
    await new Promise((res) => {
        if (els.photo.complete) return res();
        els.photo.onload = res;
    });

    const palette = extractPalette(els.photo);
    applyAccent(palette[0]);
    renderSwatches(palette);

    // Lê metadados EXIF/GPS (biblioteca exifr)
    let exif = {};
    try {
        exif = (await exifr.parse(file, { gps: true, tiff: true, ifd0: true, exif: true })) || {};
    } catch (err) {
        console.warn('Sem EXIF legível:', err);
    }

    renderMeta(file, exif, els.photo);
    renderMap(exif);
}

/* ---------- Painel: cabeçalho / narrativa ---------- */
function resetPanel(file) {
    els.place.textContent = '—';
    els.headline.textContent = file.name.replace(/\.[^.]+$/, '');
    els.dek.textContent = '';
    els.caption.textContent = '';
    els.specs.innerHTML = '';
    els.mapBlock.hidden = true;
}

function renderMeta(file, exif, img) {
    const camera = [exif.Make, exif.Model].filter(Boolean).join(' ').trim();
    const lens = exif.LensModel || '';
    const when = exif.DateTimeOriginal || exif.CreateDate || file.lastModified && new Date(file.lastModified);

    // Cabeçalho editorial
    els.place.textContent = camera || 'Sem dados de câmera';
    els.headline.textContent = when ? formatHeadlineDate(when) : file.name.replace(/\.[^.]+$/, '');
    els.dek.textContent = [lens, when ? formatTime(when) : null].filter(Boolean).join(' · ');

    // Legenda narrativa gerada dos dados
    els.caption.textContent = buildCaption(exif, when, camera);

    // Ficha técnica
    const specs = [];
    if (camera) specs.push(['Câmera', camera, true]);
    if (lens) specs.push(['Lente', lens]);
    if (exif.FocalLength) specs.push(['Distância focal', `${Math.round(exif.FocalLength)} mm`]);
    if (exif.FNumber) specs.push(['Abertura', `f/${exif.FNumber}`]);
    if (exif.ExposureTime) specs.push(['Obturador', formatShutter(exif.ExposureTime)]);
    if (exif.ISO) specs.push(['ISO', String(exif.ISO)]);
    if (when) specs.push(['Data', formatFullDate(when)]);
    specs.push(['Dimensões', `${img.naturalWidth} × ${img.naturalHeight} px`]);
    specs.push(['Proporção', aspectRatio(img.naturalWidth, img.naturalHeight)]);
    specs.push(['Arquivo', `${file.type.split('/')[1]?.toUpperCase() || '—'} · ${formatBytes(file.size)}`]);

    els.specs.innerHTML = specs.map(([k, v, serif]) => `
        <div class="spec">
            <dt class="spec__key">${k}</dt>
            <dd class="spec__val ${serif ? 'spec__val--serif' : ''}">${v}</dd>
        </div>`).join('');
}

/* Constrói uma legenda em linguagem natural a partir dos dados */
function buildCaption(exif, when, camera) {
    const parts = [];
    if (when) {
        const h = new Date(when).getHours();
        const periodo = h < 6 ? 'na madrugada' : h < 12 ? 'de manhã' : h < 18 ? 'à tarde' : 'à noite';
        parts.push(`Capturada ${periodo}`);
    }
    if (camera) parts.push(`com ${camera}`);
    if (exif.FNumber && exif.ISO) {
        const luz = exif.ISO >= 1600 ? 'em pouca luz' : exif.ISO <= 200 ? 'sob boa luz' : '';
        parts.push(`a f/${exif.FNumber}, ISO ${exif.ISO}${luz ? ' — ' + luz : ''}`);
    }
    if (!parts.length) return '';
    return parts.join(' ') + '.';
}

/* ============================================================
   Extração de paleta de cores (canvas, sem biblioteca)
   ============================================================ */
function extractPalette(img, count = 5) {
    const canvas = document.createElement('canvas');
    const size = 80; // amostra reduzida para performance
    const ratio = img.naturalWidth / img.naturalHeight;
    canvas.width = ratio >= 1 ? size : Math.round(size * ratio);
    canvas.height = ratio >= 1 ? Math.round(size / ratio) : size;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

    const data = ctx.getImageData(0, 0, canvas.width, canvas.height).data;

    // Quantiza em buckets e conta frequência
    const buckets = new Map();
    for (let i = 0; i < data.length; i += 4) {
        const a = data[i + 3];
        if (a < 125) continue;
        const r = data[i] & 0xF0, g = data[i + 1] & 0xF0, b = data[i + 2] & 0xF0;
        const key = (r << 16) | (g << 8) | b;
        const cur = buckets.get(key) || { r: 0, g: 0, b: 0, n: 0 };
        cur.r += data[i]; cur.g += data[i + 1]; cur.b += data[i + 2]; cur.n++;
        buckets.set(key, cur);
    }

    const sorted = [...buckets.values()]
        .map(c => ({ r: c.r / c.n, g: c.g / c.n, b: c.b / c.n, n: c.n }))
        .sort((a, b) => b.n - a.n);

    // Prefere a mais vibrante entre as top como accent, mantém as demais por frequência
    const top = sorted.slice(0, count).map(c => [Math.round(c.r), Math.round(c.g), Math.round(c.b)]);
    return top.length ? top : [[200, 169, 126]];
}

function applyAccent(rgb) {
    const [r, g, b] = rgb;
    document.documentElement.style.setProperty('--accent', `rgb(${r}, ${g}, ${b})`);
    // Escolhe texto claro ou escuro sobre o accent conforme luminância
    const lum = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255;
    document.documentElement.style.setProperty('--accent-ink', lum > 0.55 ? '#0e0d0c' : '#f4efe9');
}

function renderSwatches(palette) {
    els.swatches.innerHTML = palette.map(rgb => {
        const hex = rgbToHex(rgb);
        return `<div class="swatch">
            <span class="swatch__chip" style="background:${hex}"></span>
            <span class="swatch__hex">${hex.toUpperCase()}</span>
        </div>`;
    }).join('');
}

/* ============================================================
   Mapa (GPS)
   ============================================================ */
function renderMap(exif) {
    const lat = exif.latitude, lon = exif.longitude;
    if (typeof lat !== 'number' || typeof lon !== 'number') {
        els.mapBlock.hidden = true;
        return;
    }
    els.mapBlock.hidden = false;
    els.coords.textContent = `${lat.toFixed(5)}, ${lon.toFixed(5)}`;

    // (Re)inicializa o mapa
    if (mapInstance) { mapInstance.remove(); mapInstance = null; }
    mapInstance = L.map(els.map, { attributionControl: true, zoomControl: true }).setView([lat, lon], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap', maxZoom: 18,
    }).addTo(mapInstance);
    L.marker([lat, lon]).addTo(mapInstance);
    setTimeout(() => mapInstance.invalidateSize(), 200);

    // Geocodificação reversa (nome do lugar) — opcional, via Nominatim
    reverseGeocode(lat, lon);
}

async function reverseGeocode(lat, lon) {
    try {
        const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&zoom=12&accept-language=pt-BR`;
        const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
        const data = await res.json();
        const a = data.address || {};
        const place = [a.city || a.town || a.village || a.suburb, a.state, a.country]
            .filter(Boolean).join(', ');
        if (place) els.place.textContent = place;
    } catch (e) {
        /* sem rede ou bloqueado: mantém o texto atual */
    }
}

/* ============================================================
   Formatação / utilitários
   ============================================================ */
const MESES = ['janeiro','fevereiro','março','abril','maio','junho','julho','agosto','setembro','outubro','novembro','dezembro'];

function formatHeadlineDate(d) {
    d = new Date(d);
    return `${d.getDate()} de ${MESES[d.getMonth()]}, ${d.getFullYear()}`;
}
function formatFullDate(d) {
    d = new Date(d);
    return `${String(d.getDate()).padStart(2,'0')}/${String(d.getMonth()+1).padStart(2,'0')}/${d.getFullYear()}`;
}
function formatTime(d) {
    d = new Date(d);
    return `${String(d.getHours()).padStart(2,'0')}h${String(d.getMinutes()).padStart(2,'0')}`;
}
function formatShutter(t) {
    return t >= 1 ? `${t}s` : `1/${Math.round(1 / t)}s`;
}
function formatBytes(n) {
    if (n < 1024) return `${n} B`;
    if (n < 1024 * 1024) return `${(n / 1024).toFixed(0)} KB`;
    return `${(n / 1024 / 1024).toFixed(1)} MB`;
}
function aspectRatio(w, h) {
    const g = gcd(w, h);
    return `${w / g}:${h / g}`;
}
function gcd(a, b) { return b ? gcd(b, a % b) : a; }
function rgbToHex([r, g, b]) {
    return '#' + [r, g, b].map(x => x.toString(16).padStart(2, '0')).join('');
}
