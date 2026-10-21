# Escutaí - Trabalho 3: Persistência de Dados com Room Database

Aplicativo Android de reprodução de músicas, inspirado no Escutaí/Spotify, desenvolvido como projeto durante o semestre de Desenvolvimento de Aplicativos Móveis.

**Integrantes do grupo:** Felipe Kloss, Gabriel Asserman, Mariana Almeida.

---

## 📖 Sobre o Projeto e Evolução do Trabalho 2 para o Trabalho 3

No **Trabalho 2**, os dados do aplicativo ficavam inteiramente armazenados em memória utilizando `mutableStateListOf`. Embora as ações de adicionar, favoritar, editar e remover funcionassem dinamicamente na UI, todas as alterações eram perdidas quando o app ou a Activity eram destruídos.

No **Trabalho 3**, evoluímos o projeto implementando o **Room Database** (SQLite abstraction) com **KSP (Kotlin Symbol Processing)** e **Coroutines (Dispatchers.IO)**. Agora, todas as operações de CRUD (Criar, Ler, Atualizar e Deletar) são verdadeiramente persistentes.

---

## 🛠️ Arquitetura e Estrutura do Room

### 1. Entidades (`@Entity`)
Adaptamos as duas data classes principais do Trabalho 2 para se tornarem Entidades do Room:

* **`Musica`** (`@Entity(tableName = "musicas")`):
  * `@PrimaryKey(autoGenerate = true) val id: Int = 0`
  * `val titulo: String`
  * `val artista: String`
  * `val album: String`
  * `val duracaoSegundos: Int`
  * `val genero: String`
  * `val isFavorito: Boolean = false`
  * `val reproducoes: Int = 0`
  * *Escolha dos campos:* Mantivemos os atributos essenciais para identificação da mídia, formatação de tempo e contabilização de popularidade (favorito e contagem de execuções).

* **`Playlist`** (`@Entity(tableName = "playlists")`):
  * `@PrimaryKey(autoGenerate = true) val id: Int = 0`
  * `val nome: String`
  * `val descricao: String`
  * `val musicasIds: List<Int> = emptyList()`
  * *Escolha dos campos:* Permitiu relacionar as playlists às músicas através de uma lista de IDs das faixas contidas, viabilizada pelo `Converters` do Room (`TypeConverter` entre `List<Int>` e `String` separada por vírgulas).

---

### 2. DAOs (`@Dao`)
Criamos duas interfaces DAO anotadas com `@Dao`, todas utilizando `suspend fun`:

* **`MusicaDao`**:
  * `@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun inserir(musica: Musica): Long`
  * `@Query("SELECT * FROM musicas") suspend fun buscarTodos(): List<Musica>`
  * `@Update suspend fun atualizar(musica: Musica): Int`
  * `@Delete suspend fun deletar(musica: Musica): Int`
  * **Consultas adicionais/filtradas:**
    * `@Query("SELECT * FROM musicas WHERE id = :id") suspend fun buscarPorId(id: Int): Musica?` *(Requisito 3.3 - Tela de Detalhes lendo direto do banco pelo ID)*
    * `@Query("SELECT * FROM musicas WHERE isFavorito = 1") suspend fun buscarFavoritos(): List<Musica>`
    * `@Query("SELECT * FROM musicas ORDER BY reproducoes DESC") suspend fun buscarTopStats(): List<Musica>`

* **`PlaylistDao`**:
  * `@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun inserir(playlist: Playlist): Long`
  * `@Query("SELECT * FROM playlists") suspend fun buscarTodos(): List<Playlist>`
  * `@Update suspend fun atualizar(playlist: Playlist): Int`
  * `@Delete suspend fun deletar(playlist: Playlist): Int`
  * **Consulta filtrada:**
    * `@Query("SELECT * FROM playlists WHERE id = :id") suspend fun buscarPorId(id: Int): Playlist?`

---

### 3. Banco de Dados (`AppDatabase`) e Padrão Singleton
* Classe `abstract class AppDatabase : RoomDatabase()` anotada com `@Database(entities = [Musica::class, Playlist::class], version = 1)` e `@TypeConverters(Converters::class)`.
* Implementa o padrão **Singleton** recomendado:
  ```kotlin
  companion object {
      @Volatile
      private var INSTANCE: AppDatabase? = null

      fun getDatabase(context: Context): AppDatabase {
          return INSTANCE ?: synchronized(this) {
              val instance = Room.databaseBuilder(
                  context.applicationContext,
                  AppDatabase::class.java,
                  "escutai_database"
              )
              .fallbackToDestructiveMigration()
              .build()
              INSTANCE = instance
              instance
          }
      }
  }
  ```

---

### 4. Execução Assíncrona e Coroutines (`Dispatchers.IO`)
Nenhuma operação de banco é executada na thread principal (UI thread). Todas as chamadas ao DAO são gerenciadas pelo `AppRepository` ou chamadas no `LaunchedEffect(Unit)` / `rememberCoroutineScope()` através do `Dispatchers.IO`:
```kotlin
suspend fun carregarMusicas(): List<Musica> = withContext(Dispatchers.IO) {
    try {
        musicaDao.buscarTodos()
    } catch (e: Exception) {
        Log.e(TAG, "Erro ao carregar músicas do banco", e)
        emptyList()
    }
}
```

---

## 🔄 Dificuldades Encontradas e Como Resolvemos

1. **Migração de `mutableStateListOf` em memória para Room Async State:**
   * *Desafio:* No Trabalho 2, a UI dependia de mutabilidade síncrona de listas. Ao mudar para o Room, o carregamento do banco é assíncrono.
   * *Solução:* Utilizamos `LaunchedEffect(Unit)` e `remember { mutableStateOf(...) }` em cada tela, disparando buscas no `Dispatchers.IO` e atualizando o estado do Compose assim que a busca encerra, garantindo que a tela renderize suavemente sem travar a UI.

2. **Edição e Atualização de Itens (`dao.atualizar`):**
   * *Desafio:* Precisávamos de um mecanismo de UI para permitir editar informações de itens cadastrados no banco.
   * *Solução:* Adicionamos diálogos de edição modal (`AlertDialog`) nas telas de Biblioteca de Músicas e Lista de Playlists. Ao salvar, uma cópia atualizada do objeto é enviada ao `dao.atualizar(...)` no `Dispatchers.IO`.

3. **Incompatibilidade do KSP com gerações Java no AGP 9.3:**
   * *Desafio:* Na compilação inicial com KSP + Kotlin 2.x, o Room gerou classes `.java` com assinaturas `Continuation` conflitantes.
   * *Solução:* Configuramos o Room para gerar código nativo em Kotlin através da opção `ksp { arg("room.generateKotlin", "true") }` no `app/build.gradle.kts`.

---

## 📱 Teste de Aceitação e Persistência

Para testar a persistência:
1. Abra a **Biblioteca de Músicas** ou **Playlists** e adicione um novo item pelo formulário.
2. O item é inserido via `dao.inserir(...)` e aparece imediatamente na lista.
3. Feche e encerre o aplicativo completamente no gerenciador de tarefas do Android.
4. Reabra o aplicativo: o item cadastrado permanece lá salvo no SQLite local!

---

## 🖥️ Como Rodar o Projeto

1. Abra o projeto no **Android Studio**.
2. Aguarde o **Gradle Sync** (configurado com KSP e Room 2.6.1).
3. Selecione um emulador AVD ou dispositivo físico com Android API 24+.
4. Clique em **Run ▶️** (`Shift + F10`).

---

## 👥 Divisão de Tarefas da Equipe

* **Felipe Kloss:** Estrutura das entidades Room (`Musica`, `Playlist`), `AppDatabase` Singleton, `Converters` e `AppRepository` assíncrono.
* **Gabriel Asserman:** Configuração das dependências KSP/Room no Gradle, integração da `ListaMusicasScreen` e `DetalheMusicaScreen` (leitura por ID no banco).
* **Mariana Almeida:** Integração da `ListaPlaylistsScreen`, `DetalhePlaylistScreen`, `FavoritosScreen` e `TopStatsScreen` para busca e manipulação persistente no Room.
