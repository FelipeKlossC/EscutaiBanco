Escutaí

O Escutaí é um app Android de música inspirado no Spotify, desenvolvido em Kotlin com Jetpack Compose durante a matéria de Desenvolvimento de Aplicativos Móveis.

Grupo: Felipe Kloss, Gabriel Asserman e Mariana Almeida.

Neste Trabalho 4 o projeto foi reorganizado para a arquitetura MVVM, separando a camada de persistência e banco de dados, os repositórios, os viewmodels com seus estados e as telas de interface, seguindo rigorosamente os padrões recomendados em aula.

Como estava o projeto no Trabalho 3 e o que mudou:

No Trabalho 3 as telas faziam chamadas diretas ao banco Room e acumulavam o gerenciamento de estados com variáveis locais. Na reorganização para o Trabalho 4, toda a lógica de acesso a dados foi isolada em repositórios, os DAOs passaram a expor fluxos reativos com Flow, e a gestão de estado e regras de negócio foi movida para ViewModels utilizando MutableStateFlow e StateFlow. As telas tornaram-se componentes puramente visuais que observam o UiState e despacham eventos para os ViewModels.

Como foram divididos os pacotes:

O pacote data.local abriga as entidades do Room e os DAOs. O pacote data.repository contém os repositórios que interagem com os DAOs. O pacote viewmodel agrupa os UiStates, ViewModels e as Factories de criação. O pacote navigation mantém o controle de navegação entre as telas. O pacote ui.screens concentra todas as composable screens do aplicativo e o modelo de planos.

Por que o Repository existe e o que ele resolve:

O Repository atua como uma camada intermediária de abstração entre a fonte de dados (DAO) e o ViewModel. Ele resolve o acoplamento direto das consultas, garantindo que o ViewModel não conheça detalhes de implementação do banco de dados. Caso a origem dos dados mude no futuro para uma API remota ou cache local, apenas o Repository precisará ser alterado.

O que tem dentro de cada UiState:

O MusicaUiState reúne a lista de músicas, a lista de playlists, o indicador de carregamento, os campos do formulário de cadastro, o estado e os campos de edição de música, a música selecionada para o detalhe, o estado de reprodução do player, o progresso da música e propriedades calculadas como musicasFavoritas e musicasMaisOuvidas. O PlaylistUiState agrupa a lista de playlists, todas as músicas, o indicador de carregamento, os campos de cadastro de playlist, o estado de edição, a playlist selecionada, o controle de carregamento do detalhe, a visibilidade do diálogo de adição e propriedades calculadas como musicasDaPlaylist, musicasForaDaPlaylist, tempoTotalSegundos e musicaMaisOuvida.

Funcionalidades que quebraram durante a reorganização e correções:

Durante a migração para MVVM, a lista no detalhe da playlist inicialmente não atualizava ao adicionar ou remover músicas, o que foi resolvido garantindo que o ViewModel recarregasse a playlist selecionada após cada alteração no banco. Outro ponto foi a necessidade de injetar múltiplos repositórios no mesmo ViewModel para tratar exclusões em cascata nas playlists, corrigido ajustando as dependências nas Factories customizadas <!-- revisar com o grupo -->.

Dificuldade de cada integrante:

Felipe teve dificuldades na transição dos DAOs síncronos para fluxos reativos com Flow e na atualização correta dos estados compostos nos ViewModels <!-- revisar com o grupo -->. Gabriel lidou com o desafio de mapear a leitura assíncrona dos detalhes de músicas e o controle do player nas telas principais mantendo o comportamento anterior <!-- revisar com o grupo -->. Mariana enfrentou o desafio de coordenar múltiplas fontes de dados simultâneas (músicas e playlists) na tela de detalhes de playlist <!-- revisar com o grupo -->.

Tecnologias:

Kotlin, Jetpack Compose com Material 3, Navigation Compose, Room 3 com KSP, Coroutines, StateFlow, ViewModel e lifecycle-viewmodel-compose.

O que ainda não tem:

Os botões de play continuam simulados sem reprodução real de áudio, e o histórico de execuções e o plano escolhido permanecem apenas em memória durante a sessão.

Equipe:

Felipe Kloss Conceição: arquitetura MVVM, repositórios e organização dos pacotes de dados. <!-- preencher -->
Gabriel Asserman: ViewModels de música, integração das telas da biblioteca e detalhe de música. <!-- preencher -->
Mariana Almeida: ViewModels de playlist, integração das telas de playlists, favoritos e top estatísticas. <!-- preencher -->

Evidências:

Link do Google Slides: <!-- COLE AQUI: link do slide -->
Link do vídeo: <!-- COLE AQUI: link do vídeo -->

Prints das telas do App:

<img width="392" height="865" alt="Print Tela 1" src="https://github.com/user-attachments/assets/7447f283-d888-40e9-b5eb-1eebaa29ea67" />
<img width="400" height="855" alt="Print Tela 2" src="https://github.com/user-attachments/assets/92c9b888-b45c-4fd3-b2b2-13c9a9ec55a8" />
<img width="391" height="863" alt="Print Tela 3" src="https://github.com/user-attachments/assets/e18e7d2f-9bf8-4e74-84c3-72ad42e156a4" />
<img width="403" height="850" alt="Print Tela 4" src="https://github.com/user-attachments/assets/f509fee6-f8d5-494d-b9e3-b319221a06e0" />
