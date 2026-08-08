# Revelar — Visualizador de fotos

Um visualizador de fotos **diferente dos padrões de galeria/backup de hoje**.
Em vez de tratar a foto como mais um thumbnail num grid infinito, ele trata cada
imagem como um objeto rico em dados e revela a história por trás dela — com
estética **editorial/revista**: tipografia forte e metadados como legendas
elegantes.

> **Privacidade:** roda 100% no navegador. Nenhuma foto é enviada para servidor
> ou nuvem. O único acesso à rede é opcional: o mapa (OpenStreetMap) e o nome do
> lugar (geocodificação reversa) quando a foto tem GPS.

## O que ele mostra

- **A foto em tela cheia**, com a interface tingida pelas **cores dominantes** da própria imagem.
- **Paleta** extraída da foto (com códigos hex).
- **Ficha técnica (EXIF):** câmera, lente, distância focal, abertura, obturador, ISO, data/hora, dimensões, proporção e tamanho do arquivo.
- **Legenda narrativa** gerada automaticamente a partir dos dados ("Capturada à tarde com … a f/2.8, ISO 100 — sob boa luz.").
- **Mapa (GPS)** com o ponto exato e o nome do lugar, quando a foto tem coordenadas.

## Como usar

Abra o `index.html` num navegador (ou sirva a pasta):

```bash
cd photo-viewer
python3 -m http.server 8000
# depois acesse http://localhost:8000
```

Arraste uma foto para a tela (ou clique em "Escolher uma foto"). Fotos de
celular/câmera com EXIF completo mostram o máximo de informação; capturas de tela
mostram apenas o que existe no arquivo.

## Stack

- HTML + CSS + JavaScript puro (sem build).
- [`exifr`](https://github.com/MikeKovarik/exifr) — leitura de EXIF/GPS no navegador.
- [Leaflet](https://leafletjs.com/) + OpenStreetMap — mapa sem chave de API.
- Extração de paleta feita com a Canvas API (sem biblioteca).

## Ideias para evoluir

- Linha do tempo (navegação por momentos em vez de grid).
- Dados derivados: clima do dia/hora, fase da lua, nascer/pôr do sol.
- Modo imersivo (toque esconde toda a UI).
- Suporte a RAW e HDR; histograma para o modo "técnico".
