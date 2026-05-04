# Book API

書籍と著者の情報を管理する REST API です。

## 技術スタック

| 項目 | 内容 |
|------|------|
| 言語 | Kotlin |
| フレームワーク | Spring Boot |
| データアクセス | jOOQ |
| DB | PostgreSQL |
| マイグレーション | Flyway |
| ビルドツール | Gradle (Groovy) |
| JDK | 21 |

## Get Started

### 1. データベースを起動する

```bash
docker compose up -d
```

### 2. アプリケーションを起動する

```bash
./gradlew bootRun
```

## フォーマッター

```bash
./gradlew ktlintFormat   # コードを自動フォーマット
./gradlew ktlintCheck    # コードのスタイルチェック
```

## テスト

```bash
./gradlew test
```

---

## API エンドポイント

### 共通仕様

- ベース URL: `http://localhost:8080`
- リクエスト / レスポンスの形式: JSON
- 日付形式: `YYYY-MM-DD`

#### HTTPステータスコード

| ステータスコード | 説明 |
|----------------|------|
| 200 OK | 更新・取得成功 |
| 201 Created | 登録成功 |
| 400 Bad Request | バリデーションエラー・ビジネスルール違反 |
| 404 Not Found | 指定した ID が存在しない |

#### エラーレスポンス

```json
{
  "message": "Author not found: id=999"
}
```

---

### 著者 API

#### 著者登録

**概要:** 名前と生年月日を指定して著者を登録します。

**エンドポイント:**

```
POST /authors
```

**リクエストボディ:**

| フィールド名 | 型 | 必須 | 説明 | バリデーション |
|------------|-----|------|------|-------------|
| name | string | ○ | 著者名 | 空文字・空白のみ不可 |
| birthDate | string | ○ | 生年月日（`YYYY-MM-DD`） | 当日以前の日付のみ許可 |

**レスポンス（201 Created）:**

| フィールド名 | 型 | 説明 |
|------------|-----|------|
| id | number | 著者 ID |
| name | string | 著者名 |
| birthDate | string | 生年月日（`YYYY-MM-DD`） |

**使用例:**

```bash
curl -X POST http://localhost:8080/authors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "山田 太郎",
    "birthDate": "1980-01-15"
  }'
```

**レスポンス例:**

```json
{
  "id": 1,
  "name": "山田 太郎",
  "birthDate": "1980-01-15"
}
```

---

#### 著者更新

**概要:** 登録済み著者の名前・生年月日を更新します。

**エンドポイント:**

```
PUT /authors/{id}
```

**パスパラメータ:**

| パラメータ名 | 型 | 必須 | 説明 |
|------------|-----|------|------|
| id | number | ○ | 著者 ID |

**リクエストボディ:**

| フィールド名 | 型 | 必須 | 説明 | バリデーション |
|------------|-----|------|------|-------------|
| name | string | ○ | 著者名 | 空文字・空白のみ不可 |
| birthDate | string | ○ | 生年月日（`YYYY-MM-DD`） | 当日以前の日付のみ許可 |

**レスポンス（200 OK）:**

| フィールド名 | 型 | 説明 |
|------------|-----|------|
| id | number | 著者 ID |
| name | string | 著者名 |
| birthDate | string | 生年月日（`YYYY-MM-DD`） |

**使用例:**

```bash
curl -X PUT http://localhost:8080/authors/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "山田 次郎",
    "birthDate": "1980-01-15"
  }'
```

**レスポンス例:**

```json
{
  "id": 1,
  "name": "山田 次郎",
  "birthDate": "1980-01-15"
}
```

---

#### 著者別書籍一覧取得

**概要:** 指定した著者が執筆した書籍の一覧を取得します。

**エンドポイント:**

```
GET /authors/{id}/books
```

**パスパラメータ:**

| パラメータ名 | 型 | 必須 | 説明 |
|------------|-----|------|------|
| id | number | ○ | 著者 ID |

**レスポンス（200 OK）:**

書籍オブジェクトの配列。書籍が存在しない場合は空配列 `[]` を返します。

| フィールド名 | 型 | 説明 |
|------------|-----|------|
| id | number | 書籍 ID |
| title | string | タイトル |
| price | number | 価格（円） |
| publishStatus | string | 出版状況（`UNPUBLISHED` / `PUBLISHED`） |
| authors | array | 著者リスト |
| authors[].id | number | 著者 ID |
| authors[].name | string | 著者名 |

**使用例:**

```bash
curl http://localhost:8080/authors/1/books
```

**レスポンス例:**

```json
[
  {
    "id": 1,
    "title": "Kotlin 入門",
    "price": 3000,
    "publishStatus": "PUBLISHED",
    "authors": [
      { "id": 1, "name": "山田 太郎" },
      { "id": 2, "name": "鈴木 花子" }
    ]
  }
]
```

---

### 書籍 API

#### 書籍登録

**概要:** タイトル・価格・著者・出版状況を指定して書籍を登録します。

**エンドポイント:**

```
POST /books
```

**リクエストボディ:**

| フィールド名 | 型 | 必須 | 説明 | バリデーション |
|------------|-----|------|------|-------------|
| title | string | ○ | タイトル | 空文字・空白のみ不可 |
| price | number | ○ | 価格（円） | 0 以上の整数 |
| publishStatus | string | ○ | 出版状況 | `UNPUBLISHED` または `PUBLISHED` |
| authorIds | array | ○ | 著者 ID のリスト | 1 件以上・存在する著者 ID のみ |

**レスポンス（201 Created）:**

| フィールド名 | 型 | 説明 |
|------------|-----|------|
| id | number | 書籍 ID |
| title | string | タイトル |
| price | number | 価格（円） |
| publishStatus | string | 出版状況（`UNPUBLISHED` / `PUBLISHED`） |
| authors | array | 著者リスト |
| authors[].id | number | 著者 ID |
| authors[].name | string | 著者名 |

**使用例:**

```bash
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Kotlin 入門",
    "price": 3000,
    "publishStatus": "UNPUBLISHED",
    "authorIds": [1, 2]
  }'
```

**レスポンス例:**

```json
{
  "id": 1,
  "title": "Kotlin 入門",
  "price": 3000,
  "publishStatus": "UNPUBLISHED",
  "authors": [
    { "id": 1, "name": "山田 太郎" },
    { "id": 2, "name": "鈴木 花子" }
  ]
}
```

---

#### 書籍更新

**概要:** 登録済み書籍のタイトル・価格・著者リスト・出版状況を更新します。著者リストは送信した内容で全件上書きされます。出版済み（`PUBLISHED`）の書籍を未出版（`UNPUBLISHED`）に変更することはできません。

**エンドポイント:**

```
PUT /books/{id}
```

**パスパラメータ:**

| パラメータ名 | 型 | 必須 | 説明 |
|------------|-----|------|------|
| id | number | ○ | 書籍 ID |

**リクエストボディ:**

| フィールド名 | 型 | 必須 | 説明 | バリデーション |
|------------|-----|------|------|-------------|
| title | string | ○ | タイトル | 空文字・空白のみ不可 |
| price | number | ○ | 価格（円） | 0 以上の整数 |
| publishStatus | string | ○ | 出版状況 | `UNPUBLISHED` または `PUBLISHED`。`PUBLISHED` → `UNPUBLISHED` への変更は不可 |
| authorIds | array | ○ | 著者 ID のリスト（全差し替え） | 1 件以上・存在する著者 ID のみ |

**レスポンス（200 OK）:**

| フィールド名 | 型 | 説明 |
|------------|-----|------|
| id | number | 書籍 ID |
| title | string | タイトル |
| price | number | 価格（円） |
| publishStatus | string | 出版状況（`UNPUBLISHED` / `PUBLISHED`） |
| authors | array | 著者リスト |
| authors[].id | number | 著者 ID |
| authors[].name | string | 著者名 |

**使用例:**

```bash
curl -X PUT http://localhost:8080/books/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Kotlin 入門 改訂版",
    "price": 3500,
    "publishStatus": "PUBLISHED",
    "authorIds": [1, 2]
  }'
```

**レスポンス例:**

```json
{
  "id": 1,
  "title": "Kotlin 入門 改訂版",
  "price": 3500,
  "publishStatus": "PUBLISHED",
  "authors": [
    { "id": 1, "name": "山田 太郎" },
    { "id": 2, "name": "鈴木 花子" }
  ]
}
```
