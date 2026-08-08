# Revelar — app Android de visualização de fotos

Aplicativo Android nativo (Kotlin + Jetpack Compose) que revela cada foto da sua
galeria como um objeto rico em dados, com estética editorial. Uso pessoal, sem
intenção comercial e sem loja de app: você compila e instala no seu próprio
aparelho.

> **Privacidade:** toda a leitura de metadados, extração de cor e o
> reconhecimento de conteúdo por machine learning rodam **no próprio aparelho**.
> A única saída de rede é opcional: os tiles do mapa (OpenStreetMap) e o nome do
> lugar (geocodificação reversa), acionados apenas quando a foto tem GPS.

## O que ele faz

- Abre a **galeria real** do aparelho em um grid.
- Ao tocar numa foto, exibe em **tela cheia com a interface tingida pela cor dominante** da imagem.
- Painel editorial deslizante com:
  - **EXIF completo:** câmera, lente, distância focal, abertura, obturador, ISO, data e hora.
  - **Legenda narrativa** gerada dos dados.
  - **Paleta** de cores extraída da foto.
  - **O que a foto mostra:** etiquetas de conteúdo reconhecidas pelo ML Kit, offline.
  - **Mapa** com o ponto exato e o **nome do lugar**, quando há GPS.

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem / UI | Kotlin + Jetpack Compose (Material 3) |
| Galeria | MediaStore |
| Imagens | Coil |
| EXIF / GPS | androidx.exifinterface |
| Paleta | androidx.palette |
| Reconhecimento de conteúdo | ML Kit Image Labeling (no dispositivo) |
| Mapa | osmdroid + OpenStreetMap (sem chave de API) |
| Permissões | Accompanist Permissions |

## Como rodar (a partir do Windows)

1. Instale o **Android Studio** (versão recente, com o Android SDK).
2. Abra a pasta `photo-viewer-android` como projeto (`File > Open`).
3. Deixe o Android Studio fazer o **Gradle Sync**. Ele baixa as dependências e
   gera o Gradle Wrapper automaticamente. Se sugerir atualizar alguma versão de
   plugin ou biblioteca, aceite.
4. Ative as **Opções do desenvolvedor** e a **Depuração USB** no seu Android,
   conecte por cabo e autorize o computador.
5. Selecione o aparelho na barra superior e clique em **Run** (▶).

O app pede permissão de acesso às fotos na primeira execução. Para ver GPS e mapa,
use fotos tiradas com o GPS ativado.

### Observações de versão

As versões das dependências estão fixadas em `gradle/libs.versions.toml`. Como o
ecossistema Android atualiza rápido, o Android Studio pode sugerir versões mais
novas do AGP, do Kotlin ou do Compose BOM no primeiro sync; aceitar as sugestões
costuma resolver eventuais incompatibilidades.

## Próximos passos possíveis

- Linha do tempo (navegar por momentos, não por grid).
- Busca por conteúdo ("praia", "cachorro") usando as etiquetas já geradas.
- Dados derivados: clima do dia/hora e pôr do sol, cruzando GPS + data.
- Gesto de pinça para zoom e modo totalmente imersivo.
- Suporte a Live Photos e dados de profundidade do modo retrato.
