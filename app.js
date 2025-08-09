// Array para armazenar todos os nomes dos participantes.

let amigos = [];


// * Adiciona um novo amigo à lista.

function adicionarAmigo() {
    
    // Obtém o elemento de input e o seu valor.
    
    let campoAmigo = document.getElementById('amigo');
    let nome = campoAmigo.value.trim();

    // Valida se um nome foi realmente digitado.
    
    if (nome === '') {
        alert('Por favor, digite o nome do amigo.');
        return;
    }

    // Verificar se o nome já foi adicionado (ignorando maiúsculas/minúsculas).
    
    if (amigos.some(amigoExistente => amigoExistente.toLowerCase() === nome.toLowerCase())) {
        alert(`O nome "${nome}" já foi adicionado. Por favor, insira um nome diferente.`);
        campoAmigo.value = '';
        return;
    }

    // Adiciona o nome ao array e atualiza a lista na tela.
    
    amigos.push(nome);
    atualizarListaAmigos();

    // Limpa o campo de input e coloca o foco nele novamente.
    
    campoAmigo.value = '';
    campoAmigo.focus();
}

// Atualiza a lista de amigos exibida na página.

function atualizarListaAmigos() {

    // Obtém o elemento <ul> onde a lista é exibida e limpa a lista atual para evitar duplicatas.

    let listaAmigosElemento = document.getElementById('listaAmigos');
    listaAmigosElemento.innerHTML = '';

    // Cria e adiciona um item <li> para cada amigo na lista.
    
    for (let i = 0; i < amigos.length; i++) {
        let itemLista = document.createElement('li');
        itemLista.textContent = amigos[i];
        listaAmigosElemento.appendChild(itemLista);
    }
}

// Realiza o sorteio do amigo secreto.

function sortearAmigo() {
 
    // Obtém o elemento onde o resultado será exibido e o limpa.
 
    let resultadoElemento = document.getElementById('resultado');
    resultadoElemento.innerHTML = '';

    // Verifica se há participantes suficientes para o sorteio - necessário, no minimo, dois.
 
    if (amigos.length < 2) {
        alert('Por favor adicionar, pelo menos, 2 amigos para realizar o sorteio!');
        return;
    }

    // Embaralha a lista de amigos.
   
    for (let i = amigos.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [amigos[i], amigos[j]] = [amigos[j], amigos[i]];
    }

    // Exibe o resultado do sorteio.
   
    for (let i = 0; i < amigos.length; i++) {
        let amigoSorteado = (i === amigos.length - 1) ? amigos[0] : amigos[i + 1];

        // Cria um elemento de lista para mostrar o par sorteado.
        
        let itemResultado = document.createElement('li');
        itemResultado.textContent = `${amigos[i]} → ${amigoSorteado}`;
        resultadoElemento.appendChild(itemResultado);
    }
}

// Limpa a lista de amigos e o resultado do sorteio anterior para iniciar um novo sorteio.

function novoSorteio() {
    amigos = [];
    document.getElementById('listaAmigos').innerHTML = '';
    document.getElementById('resultado').innerHTML = '';
}