🎲 Projeto Amigo Secreto Uma aplicação web simples e divertida para realizar sorteios de Amigo Secreto (ou Amigo Oculto) de forma rápida, digital e justa. Chega de cortar papéizinhos e correr o risco de alguém sortear a si mesmo!

Este projeto foi criado com HTML, CSS e JavaScript puros, focando em uma interface intuitiva e na lógica correta para um sorteio circular.

🖼️ Telas de Demonstração Veja como a aplicação se parece em cada etapa do processo:

Tela Inicial Ao abrir a página, você encontra uma interface limpa, pronta para começar a adicionar os participantes.

Adicionando Amigos À medida que os nomes são adicionados, eles aparecem em uma lista, confirmando quem já está participando do sorteio.

Resultado do Sorteio Após clicar em "Sortear amigo", o resultado é exibido de forma clara, com cada participante e a pessoa que ele irá presentear.

✨ Funcionalidades Adição de Participantes: Adicione quantos amigos quiser à lista do sorteio.

Validação de Nomes: O sistema impede a adição de nomes em branco ou duplicados.

Sorteio Inteligente: A lógica garante um sorteio circular (A tira B, B tira C, ..., Z tira A), o que impede que alguém se auto-sorteie ou que alguém fique de fora.

Reiniciar Sorteio: Com o botão "Novo sorteio", é possível limpar todos os dados e começar uma nova brincadeira a qualquer momento.

Interface Responsiva: O design se adapta bem a diferentes tamanhos de tela (embora otimizado para visualização desktop).

🚀 Como Funciona o Jogo Utilizar a aplicação é muito simples:

Digite o Nome: No campo "Digite um nome", insira o nome do primeiro participante.

Adicione à Lista: Clique no botão "Adicionar". O nome aparecerá na lista de "Amigos adicionados".

Repita o Processo: Continue adicionando todos os participantes do seu grupo. Você precisa de no mínimo 2 pessoas para sortear.

Sorteie!: Quando todos estiverem na lista, clique no botão "Sortear amigo".

Veja o Resultado: A lista de resultados aparecerá abaixo, mostrando as duplas (quem presenteia → quem recebe).

Comece de Novo: Para limpar tudo e iniciar um novo sorteio, basta clicar em "Novo sorteio".

🛠️ Tecnologias Utilizadas Este projeto foi construído utilizando tecnologias web fundamentais, sem a necessidade de frameworks ou bibliotecas externas.

HTML5: Para a estrutura semântica da página.

CSS3: Para a estilização, layout (Flexbox) e design visual.

JavaScript (ES6+): Para toda a lógica funcional, manipulação do DOM e interatividade do jogo.

📁 Estrutura dos Arquivos O repositório está organizado da seguinte forma:

/ |-- 📄 index.html # Arquivo principal da estrutura da página |-- 🎨 style.css # Folha de estilos para o visual |-- ⚙️ app.js # Código com toda a lógica do sorteio |-- 🖼️ assets/ # Pasta com as imagens utilizadas na interface | |-- amigo-secreto.png | |-- play_circle_outline.png | |-- replay_circle_outline.png |-- 📖 README.md # Este arquivo 🏃 Como Executar o Projeto Como este é um projeto baseado apenas em arquivos estáticos, você não precisa de um servidor ou de processos de compilação.

Clone este repositório:

Bash

git clone https://github.com/seu-usuario/seu-repositorio.git Navegue até a pasta do projeto.

Abra o arquivo index.html no seu navegador de preferência.

E pronto! O jogo estará funcionando.
