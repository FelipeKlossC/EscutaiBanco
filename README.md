Escutaí

O Escutaí é um app Android de música inspirado no Spotify (mas não é cópia, juro). Foi feito em Kotlin com Jetpack Compose durante a matéria de Desenvolvimento de Aplicativos Móveis.

Grupo: Felipe Kloss, Gabriel Asserman e Mariana Almeida.

Com ele dá pra cadastrar músicas e playlists, favoritar faixas, ver o ranking das mais ouvidas, consultar o histórico e simular a contratação de um plano VIP. Tudo fica salvo no banco local do aparelho (Room), então os dados continuam lá mesmo depois de fechar e abrir o app de novo.

Neste Trabalho 4 a gente não criou nenhuma tela nem funcionalidade nova. O app faz exatamente o que fazia no Trabalho 3, só que o código foi reorganizado em camadas seguindo o MVVM visto em aula: Model, Repository, ViewModel e View.

Documentação e evidências:

A documentação do trabalho (com as respostas do enunciado, prints e vídeos) está na pasta docs do repositório.

Prints das telas do app:

<img width="392" height="865" alt="Print Tela 1" src="https://github.com/user-attachments/assets/4ee4690b-2be6-4399-b8af-773969356f63" />
<img width="400" height="855" alt="Print Tela 2" src="https://github.com/user-attachments/assets/6c60864f-7823-4ad1-9031-d7f65af8a6b6" />
<img width="391" height="863" alt="Print Tela 3" src="https://github.com/user-attachments/assets/8eed7c7e-734f-4617-b0a5-5ef7334c3662" />
<img width="403" height="850" alt="Print Tela 4" src="https://github.com/user-attachments/assets/f8fff74c-41f7-4521-883e-205bfcf58a24" />
<img width="397" height="846" alt="Print Tela 8" src="https://github.com/user-attachments/assets/5d0716f1-1d09-4d03-8a35-99af9da890a6" />
<img width="402" height="853" alt="Print Tela 7" src="https://github.com/user-attachments/assets/e063924d-69a3-4191-937b-ee63db820a96" />
<img width="385" height="851" alt="Print Tela 6" src="https://github.com/user-attachments/assets/bb9e0113-20f8-4fae-afc0-81926c8513e9" />
<img width="399" height="852" alt="Print Tela 5" src="https://github.com/user-attachments/assets/c9799096-adc9-4bb0-ad09-ea88b7074522" />

Como estava o projeto no Trabalho 3 e o que mudou:

No Trabalho 3 as próprias telas conversavam com o banco. Cada tela pegava o DAO, chamava funções suspend soltas com try/catch dentro de um CoroutineScope(Dispatchers.IO).launch, buscava a lista ao abrir com um LaunchedEffect e precisava buscar tudo de novo na mão depois de cada inserir, editar ou remover. Além disso, o estado de cada tela ficava espalhado em várias variáveis remember.

No Trabalho 4 a gente separou tudo isso. O DAO agora devolve a lista como Flow, então o Room avisa sozinho quando a tabela muda e ninguém precisa recarregar nada. As telas não falam mais com o DAO: só o Repository fala com ele, e só o ViewModel fala com o Repository. O ViewModel guarda o estado da tela num UiState (MutableStateFlow por dentro e StateFlow por fora), e a tela só observa esse estado e chama os métodos do ViewModel quando o usuário faz alguma coisa. O try/catch com Log.e que ficava nas telas foi pros ViewModels.

Como dividimos os pacotes:

O pacote data.local tem as entidades do Room (Musica e Playlist), os DAOs e o AppDatabase. O pacote data.repository tem o MusicaRepository e o PlaylistRepository. O pacote viewmodel tem os UiStates, os ViewModels e as Factories. O pacote ui.screens tem todas as telas (e a data class Plano, que só serve pra tela de Planos), e o ui.theme guarda cores e tema. O pacote navigation continua com as rotas e a barra de baixo.

Nas palavras do MVVM: o Model é o data.local, o Repository é o data.repository, o ViewModel é o viewmodel e a View são as telas do ui.screens.

com.example.myapplication/
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── Musica.kt
│   │   ├── Playlist.kt
│   │   ├── MusicaDao.kt
│   │   ├── PlaylistDao.kt
│   │   └── AppDatabase.kt
│   └── repository/
│       ├── MusicaRepository.kt
│       └── PlaylistRepository.kt
├── viewmodel/
│   ├── MusicaUiState.kt
│   ├── MusicaViewModel.kt
│   ├── MusicaViewModelFactory.kt
│   ├── PlaylistUiState.kt
│   ├── PlaylistViewModel.kt
│   └── PlaylistViewModelFactory.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── Rotas.kt
└── ui/
    ├── screens/
    └── theme/

Por que o Repository existe e o que ele resolve:

Antes, quem sabia como o dado era buscado era a própria tela. Agora o Repository fica no meio, entre o DAO e o ViewModel, e só repassa as chamadas (buscar tudo, inserir, atualizar, deletar e buscar por id). Com isso o ViewModel não precisa saber nada do Room. Se um dia os dados vierem de outro lugar, como uma API, só o Repository muda, e o ViewModel e a tela continuam iguais.

O que tem dentro de cada UiState:

O MusicaUiState tem a lista de músicas, a lista de playlists (usada pra limpar o id da música das playlists quando ela é excluída), se está carregando, os campos do formulário de cadastro e a mensagem de erro, a música que está sendo editada com os campos da janela de edição, a música selecionada pro detalhe, se o player está tocando e o progresso dele. Ele também tem propriedades calculadas, como musicasFavoritas, musicasMaisOuvidas, totalMusicas e totalPlaylists, que são usadas em Favoritos, Top e Perfil.

O PlaylistUiState tem a lista de playlists, todas as músicas, se está carregando, os campos do formulário de criar playlist e o erro, a playlist em edição com seus campos, a playlist selecionada pro detalhe e se o diálogo de adicionar música está aberto. As propriedades calculadas são musicasDaPlaylist, musicasForaDaPlaylist, tempoTotalSegundos e musicaMaisOuvida, que alimentam as métricas do detalhe da playlist.

A gente escolheu esses campos porque são tudo o que cada tela precisa pra se desenhar. Assim a tela não guarda estado nenhum por conta própria.

O que quebrou no caminho:

No detalhe da playlist, a lista de músicas não atualizava depois de adicionar ou remover uma música. Isso foi resolvido fazendo o ViewModel buscar a playlist de novo no banco depois de cada alteração. Outra coisa foi que excluir uma música precisa tirar o id dela das playlists, então o MusicaViewModel passou a receber também o PlaylistRepository, e a Factory teve que ser ajustada pra passar os dois.

Dificuldade de cada um:

O Felipe teve dificuldade em entender a troca da busca única (que precisava ser recarregada na mão) pelo Flow, e em manter o estado dos ViewModels certo depois de cada ação. O Gabriel teve que adaptar a leitura dos detalhes da música, que é assíncrona, e o controle do player, mantendo o comportamento de antes. A Mariana teve que lidar com duas fontes de dados ao mesmo tempo (músicas e playlists) na tela de detalhe da playlist.

Tecnologias:

Kotlin, Jetpack Compose com Material 3, Navigation Compose, Room 3.0.3 com KSP, Coroutines, Flow e StateFlow, ViewModel (lifecycle-viewmodel-compose), Gradle 9.5 com Kotlin DSL e Android Gradle Plugin 9.x. O módulo usa minSdk 24, targetSdk 35, compileSdk 35 e Java 11.

Como rodar:

Você vai precisar de uma versão recente do Android Studio, do Android SDK 35 e de um emulador ou celular com Android 7.0 (API 24) ou superior. Na primeira vez também precisa de internet pra baixar as dependências.

Clone o repositório: git clone (link do repositório)
No Android Studio, vá em File → Open e escolha a pasta do projeto.
Espere o Gradle Sync terminar (na primeira vez demora um pouco, porque ele também roda o KSP do Room).
Escolha um emulador ou conecte o celular e clique em Run.

O banco começa vazio, então músicas e playlists aparecem conforme você cadastra pelo app.

O que ainda não tem:

Os botões de play são simulados e não tocam áudio de verdade. O histórico de execuções e o plano escolhido na tela de Planos ficam só em memória, então voltam ao início quando o app é fechado (músicas, playlists, favoritos e reproduções continuam salvos no banco). Os planos VIP e o perfil são só demonstração, sem login nem pagamento.

Equipe:

Felipe Kloss Conceição: arquitetura MVVM, repositórios e organização dos pacotes de dados. Gabriel Asserman: ViewModels de música e integração das telas da biblioteca e do detalhe de música. Mariana Almeida: ViewModels de playlist e integração das telas de playlists, favoritos e top.
