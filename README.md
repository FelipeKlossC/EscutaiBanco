Escutaí

O Escutaí é um app Android de música inspirado no Spotify (mas não é cópia, juro). Foi feito durante o semestre na matéria de Desenvolvimento de Aplicativos Móveis, em Kotlin com Jetpack Compose.

Grupo: Felipe Kloss, Gabriel Asserman e Mariana Almeida.

Com ele dá pra navegar por uma biblioteca de músicas, criar playlists, favoritar faixas, ver o ranking das mais ouvidas, consultar o histórico e simular a contratação de um plano VIP. Além do básico de um player, a gente colocou umas coisas próprias, como as métricas de cada playlist (duração total e música destaque) e o ranking de reproduções.

Neste Trabalho 3 o app deixou de guardar tudo na memória e passou a salvar os dados num banco local com Room (SQLite). Agora o que você adiciona, edita ou remove fica gravado no aparelho, e continua lá mesmo depois de fechar e abrir o app de novo.

Segue vídeo com o teste de persistência e demonstração das telas e funções do app novamente em arquivo zip junto à entrega no blackboard

Como estava o Trabalho 2 e o que mudou:

No Trabalho 2 os dados ficavam num AppRepository com listas mutableStateListOf. Adicionar, favoritar e remover funcionavam e a tela atualizava na hora, mas era tudo na memória: fechou o app, perdeu tudo e voltava pros dados iniciais.

No Trabalho 3 a gente evoluiu o mesmo projeto pra usar Room com KSP e Coroutines, seguindo o padrão do exemplo visto em aula. As data classes Musica e Playlist viraram @Entity e ganharam seus DAOs, e o AppRepository deixou de existir: agora as telas pegam o banco com AppDatabase.getDatabase(context) e chamam o DAO através de funções suspend com try/catch e Log.e em cada operação. As telas de lista buscam os dados no banco quando abrem, e as telas de detalhe buscam o item direto pelo id que chega na rota. Também implementamos a edição de músicas e playlists, que não existia antes. Os planos VIP continuam como no Trabalho 2, uma lista dentro da própria tela de Planos, e não entram no banco.

O que o app faz:

Na tela inicial tem o player, com play/pause, anterior, próxima, aleatório, repetir e barra de progresso, além das playlists em destaque. A biblioteca de músicas deixa cadastrar, listar, editar e remover faixas (título, artista, duração e gênero). Dá pra favoritar músicas com o coração e ver todas numa tela só. As playlists podem ser criadas, editadas, excluídas e abertas, e dentro de cada uma dá pra adicionar e remover músicas. O detalhe da playlist mostra o total de faixas, a duração total em minutos e a música mais ouvida. O detalhe da música mostra álbum, gênero, duração e total de execuções, e cada vez que você toca a faixa o contador sobe e é salvo no banco. Tem também o Top Músicas (ranking por reproduções), o Histórico de execuções, o Perfil com um resumo da atividade e a tela de Planos VIP, que é só simulada.

A navegação é feita com Navigation Compose, com uma barra inferior de 5 abas: Início, Músicas, Playlists, Favoritos e Top. As outras telas abrem a partir delas: o Perfil pelo avatar da Home, Planos e Histórico pelo Perfil, e os detalhes ao tocar num item das listas.

Como funciona o Room no projeto:

Temos duas entidades, as mesmas data classes do Trabalho 2, ambas com @PrimaryKey(autoGenerate = true) no id.

A Musica (tabela musicas) tem título, artista, álbum, duração em segundos, gênero, isFavorito e reproducoes. Escolhemos esses campos porque eles dão conta de identificar a faixa, calcular as métricas e alimentar as telas de Favoritos e Top.

A Playlist (tabela playlists) tem nome, descrição e os ids das músicas (musicasIds). A gente guardou só os ids pra não duplicar dado: a música existe uma vez só e a playlist aponta pra ela. Como o Room não salva List<Int> direto e a gente não usa TypeConverter, o campo é um texto com os ids separados por vírgula (por exemplo "1,2,3"), e a própria classe tem a função listaDeIds() que transforma esse texto em List<Int>. Pra gravar, usamos copy(musicasIds = lista.joinToString(",")).

Cada entidade tem seu DAO (MusicaDao e PlaylistDao), todos com os quatro métodos básicos como suspend fun: inserir, buscarTodos, atualizar e deletar. Além deles, cada DAO tem só uma consulta extra:

buscarPorId(id) nos dois DAOs, com WHERE id = :id, usado pelos detalhes da música e da playlist pra ler direto do banco (é o requisito 3.3 do enunciado).

As telas de Favoritos e de Top Músicas não têm consulta própria: elas usam buscarTodos() e filtram ou ordenam no código Kotlin (filter { it.isFavorito } e sortedByDescending { it.reproducoes }).

O banco é a classe AppDatabase, uma abstract class que estende RoomDatabase, com version = 1, as duas entidades e o arquivo chamado app_database. Ela segue o padrão Singleton (companion object com @Volatile e synchronized) e é pega nas telas com AppDatabase.getDatabase(context). O banco começa vazio: não tem dados de exemplo, então músicas e playlists aparecem conforme a gente cadastra pelo app.

Nenhuma operação de banco roda na thread principal. Os métodos dos DAOs são suspend e toda chamada a eles é feita dentro de CoroutineScope(Dispatchers.IO).launch { ... }, dentro de funções suspend com try/catch. A busca inicial de cada tela é feita num LaunchedEffect(Unit), que chama CoroutineScope(Dispatchers.IO).launch pra buscar os dados.

Dificuldades que a gente teve:

Sair do mutableStateListOf pro Room. No Trabalho 2 a lista era síncrona e a tela atualizava sozinha. Com o Room a leitura é assíncrona, então a lista começa vazia até o banco responder. A gente resolveu guardando a lista em remember { mutableStateOf(...) } em cada tela, buscando no banco com LaunchedEffect e atualizando o estado quando a consulta termina. Depois de inserir, editar ou remover, a tela busca de novo pra refletir a mudança. Também colocamos um isLoading pra tela não ficar piscando vazia.

Edição de itens. O Trabalho 2 não tinha edição. Criamos diálogos (AlertDialog) de edição na Biblioteca de Músicas e na lista de Playlists. Ao salvar, o app faz um copy(...) do objeto e manda pro dao.atualizar(...).

Playlist e música sem chave estrangeira. Como a playlist guarda só os ids, ao remover uma música o id dela ficaria dentro das playlists. Então a função deletarMusica, depois de apagar a música, passa pelas playlists e tira aquele id de cada uma.

Room 3 e KSP no Gradle. A gente usou o mesmo setup do exemplo de aula (Room 3, pacote androidx.room3, com o plugin do KSP), então foi preciso trocar as dependências antigas e os imports das entidades e DAOs. O plugin do KSP precisa estar declarado antes da linha ksp(room3-compiler), senão o Sync não reconhece o compilador do Room.

Dificuldades de cada um:

Felipe: a maior dificuldade foi o Room não aceitar campos do tipo lista. Enquanto a Playlist tinha List<Int>, o projeto não compilava e o erro dizia que o Room não sabia como salvar aquele campo. Como a gente não usa TypeConverter, ele trocou o campo por um texto com os ids separados por vírgula e criou a função listaDeIds() dentro da própria Playlist pra converter de volta. Outro ponto foi tirar o AppRepository e passar as chamadas pro padrão do exemplo, com funções suspend e try/catch, sem esquecer nenhuma tela que ainda usava o repositório.

Gabriel: ele ficou com a parte do Gradle e teve que acertar a ordem dos plugins, já que o id do KSP precisa estar sincronizado antes de adicionar a linha do ksp(room3-compiler), e ainda trocou as dependências antigas pelas do Room 3. Na tela de Detalhe da Música, o desafio foi que a busca por id devolve um valor que pode ser nulo e demora um pouco pra chegar. Ele tratou com o estado de carregando e com a mensagem de música não encontrada, e também precisou atualizar a tela depois de favoritar e de tocar a faixa, buscando a música de novo no banco.

Mariana: no Detalhe da Playlist a tela precisa de três informações do banco ao mesmo tempo (a playlist, as músicas dela e a lista completa pra tela de adicionar), e no começo as métricas e a lista não atualizavam depois de adicionar ou remover uma música. Ela resolveu recarregando os três dados depois de cada alteração. Em Favoritos e Top Músicas, como não existem consultas próprias, ela usou o buscarTodos() e fez o filtro dos favoritos e a ordenação por reproduções no código da tela.

Tecnologias:

Kotlin, Jetpack Compose com Material 3, Navigation Compose, Room 3.0.3 (room3-runtime) com KSP 2.3.4 (room3-compiler), Coroutines, Material Icons Extended, Gradle 9.5 com Kotlin DSL e Android Gradle Plugin 9.x. O módulo usa minSdk 24, targetSdk 35, compileSdk 35 e Java 11.

Estrutura do projeto:

app/src/main/java/com/example/myapplication/ ├── MainActivity.kt # Ponto de entrada (tema e navegação) ├── data/ │ └── db/ │ ├── AppDatabase.kt # Database do Room (Singleton) │ ├── MusicaDao.kt │ └── PlaylistDao.kt ├── model/ │ ├── Musica.kt # @Entity │ ├── Playlist.kt # @Entity │ └── Plano.kt # data class comum (não é do banco) ├── navigation/ │ ├── AppNavigation.kt # NavHost + barra inferior │ └── Rotas.kt └── ui/ ├── screens/ # Todas as telas └── theme/ # Cores, tipografia e tema

Como rodar:

Você vai precisar de uma versão recente do Android Studio (o projeto usa AGP 9.x e Gradle 9.5, então vale usar a estável mais atual), do Android SDK 35 (o Android Studio oferece instalar sozinho ao abrir o projeto), de um emulador ou de um celular com depuração USB ativada (Android 7.0 / API 24 ou superior) e de internet na primeira execução, pra baixar as dependências.

Clone o repositório: git clone (link do repositório)
No Android Studio, vá em File → Open e escolha a pasta do projeto.
Espere o Gradle Sync terminar (na primeira vez pode demorar uns minutos, porque ele também roda o KSP do Room).
Crie um emulador em Tools → Device Manager ou conecte seu celular.
Selecione o dispositivo e clique em Run ▶️ (ou Shift + F10).

Se der problema, o jeito é o de sempre (é Android Studio, né):

Erro de SDK: em Tools → SDK Manager, instale o Android SDK 35.
Erro de JDK: em Settings → Build, Execution, Deployment → Build Tools → Gradle, escolha o JDK embutido do Android Studio (jbr).
Sync falhando: confira a internet e use File → Sync Project with Gradle Files.
Dados estranhos ou erro ao abrir o app depois de mexer nas tabelas: como a gente não usa migração, se uma entidade for alterada o Room não consegue abrir o banco antigo. Pra começar do zero, desinstale o app ou use Clear Storage nas configurações dele.

O que ainda não tem:

Os botões de play não tocam áudio de verdade: eles só alternam entre tocando e pausado e aumentam o contador de execuções. O histórico de execuções (historicoMusicas) e o plano escolhido na tela de Planos ficam apenas em memória durante a sessão e não são persistidos no Room, portanto voltam ao início ao fechar e reabrir o app (músicas, playlists, favoritos e reproduções são persistidos e mantidos no banco de dados). Os planos VIP e o perfil são só demonstração, sem login nem pagamento. O banco é local, sem servidor, API ou sincronização na nuvem, como o enunciado pediu. E os botões de compartilhar, anterior, próxima, aleatório e repetir ainda não fazem nada.

Equipe:

Felipe Kloss Conceição: telas de Perfil, Planos e Histórico; README e documentação. No Trabalho 3: entidades do Room (Musica e Playlist), AppDatabase Singleton e remoção do AppRepository.
Gabriel Asserman: tela Início, navegação (barra inferior e rotas), Biblioteca de Músicas, Detalhe da Música e Favoritos. No Trabalho 3: dependências KSP/Room no Gradle, integração da Biblioteca de Músicas e do Detalhe da Música (leitura por id no banco).
Mariana Almeida: Playlists, Detalhe da Playlist (métricas) e Top Músicas. No Trabalho 3: integração de Playlists, Detalhe da Playlist, Favoritos e Top Músicas com o Room.

Prints das telas do App:

<img width="392" height="865" alt="Print Tela 1" src="https://github.com/user-attachments/assets/7447f283-d888-40e9-b5eb-1eebaa29ea67" />
<img width="400" height="855" alt="Print Tela 2" src="https://github.com/user-attachments/assets/92c9b888-b45c-4fd3-b2b2-13c9a9ec55a8" />
<img width="391" height="863" alt="Print Tela 3" src="https://github.com/user-attachments/assets/e18e7d2f-9bf8-4e74-84c3-72ad42e156a4" />
<img width="403" height="850" alt="Print Tela 4" src="https://github.com/user-attachments/assets/f509fee6-f8d5-494d-b9e3-b319221a06e0" />
<img width="397" height="846" alt="Print Tela 8" src="https://github.com/user-attachments/assets/8fae3703-b78b-40ff-be17-ed5c41203922" />
<img width="402" height="853" alt="Print Tela 7" src="https://github.com/user-attachments/assets/d7333775-573f-4299-9924-8fd995a6bad0" />
<img width="385" height="851" alt="Print Tela 6" src="https://github.com/user-attachments/assets/cb84937b-d1d7-4107-9ec7-e83e47472849" />
<img width="399" height="852" alt="Print Tela 5" src="https://github.com/user-attachments/assets/03f530d5-caaf-4b3e-8e8a-404f77cb3749" />
