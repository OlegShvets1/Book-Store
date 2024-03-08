# 📗📘📙 Online Book Store 📗📘📙   

   The main task of our project is to enable even the most demanding book connoisseurs to make their search easy and pleasant.
   This product allows users to access a wide range of literary works through a convenient online platform in just a few clicks. We have created a simple and user-friendly interface that allows you to comfortably find the book you want, add it to the cart and manage your order until you receive it.
   The constant updating of the book assortment, which is provided by our administrators, will allow you to plunge into an interesting and exciting journey among the lines of your favorite books...

 
## ⚙️ Technologies
**The project is built using mainly Spring Framework. Here is a complete list:**

+ Spring Boot
+ Spring Security
+ Spring Boot Web
+ Spring Data JPA
+ JWT
+ Lombok
+ Mapstruct
+ Swagger
+ MySQL
+ Liquibase
+ Docker
+ Docker Testcontainers using MySQL

## 🏔️ Project endpoints:

 ## 🔑Authentication

### Post
`/api/auth/registration` - *registers new user, accessible without any role.*

Exemplary link:
`http://localhost:8080/api/auth/registration`

**Exemple request:**
```json
{
    "email": "bob@example.com",
    "password": "12345678",
    "repeatPassword": "12345678",
    "firstName": "Bob",
    "lastName": "Alison",
    "shippingAddress": "Bob`s address"
}
```
**Response status code**: 201

**Exemple response:**
```json
{
    "id": 1,
    "email": "bob@example.com",
    "firstName": "Bob",
    "lastName": "Alison",
    "shippingAddress": "Bob`s address"
}
```
---

`/api/auth/login` - *log in with registered user,  accessible forall users.*

**Exemple link:**
`http://localhost:8080/api/auth/login`

**Exemple request:**
```json
{
    "email": "bob@example.com",
    "password": "12345678"
}
```
**Response status**: 201

**Exemple response:**
```json
{
  "token": "UTJhbGciOiJIUzI1NiJ9.NyJzdWIiOiJib2JAZXhhbXBsZS5jb20iLCJpPOQiOjE3MDg1NTO3OTEsImV4cCI6MTcwODU1MTA5MX0.FQOptkrB5WwoFdEU7B7hi9S_AZEE5Kk927xUSBhJ4Oi"
}
```

## 📗📘📙 Book

### Get
`/api/books` - *returns a list of all stored books accessible for roles User, Admin.*

**Exemple link:**
`http://localhost:8080/api/books`

**Response status code**: 200

**Exemple response:**
```json
{
    "id": 1,
    "title": "White Fang",
    "author": "Jack London",
    "isbn": "00000000000001",
    "price": 19.90,
    "description": "Book about adventure",
    "coverImage": "http://example.com/whiteFang.jpg",
    "categoryIds": [
      1
    ]
}
```
---

`/api/books/{id}` - *returns book by the specified id value accessible for roles User, Admin.*

**Exemplary link:**
`http://localhost:8080/api/books/1`

**Response status code**: 200

**Exemple response:**
```json
{
    "id": 1,
    "title": "White Fang",
    "author": "Jack London",
    "isbn": "00000000000001",
    "price": 19.90,
    "description": "Book about adventure",
    "coverImage": "http://example.com/whiteFang.jpg",
    "categoryIds": [
      1
    ]
}
```
---

`/api/books/search` - *searches books using specified parameters, accessible for roles User, Admin.*

**Exemple link:**
`http://localhost:8080/api/books/search?titles=White Fang&author=Jack London`

**Response status code**: 200

**Exemple response:**
```json
{
    "id": 1,
    "title": "White Fang",
    "author": "Jack London",
    "isbn": "00000000000001",
    "price": 19.90,
    "description": "Book about adventure",
    "coverImage": "http://example.com/whiteFang.jpg",
    "categoryIds": [
      1
    ]
}
```

### Post
`/api/books` - *creates a new book in the database, accessible for role Admin.*

**Exemple request:**
```json
{
    "title": "New book",
    "author": "New author",
    "isbn": "00000000000002",
    "price": 29.99,
    "description": "New description",
    "coverImage": "https://example.com/updatedbook-cover-image.jpg"
}
```

**Response status code**: 201

**Exemplary response:**
```json
{
    "id": 1,
    "title": "New book",
    "author": "New author",
    "isbn": "00000000000002",
    "price": 29.99,
    "description": "New description",
    "coverImage": "https://example.com/updatedbook-cover-image.jpg",
    "categoryIds": []
}
```

### Put
`/api/books/{id}` - *updates the books with specified id, accessible for role Admin.* 

**Exemple link:**
`http://localhost:8080/api/books/1`

**Response status code**: 202

**Exemple request:**
```json
{
    "title": "Shantaram",
    "author": "Greg David Roberts",
    "isbn": "00000000000003",
    "price": 27.90,
    "description": "Book about adventure",
    "coverImage": "https://example.com/shantaram-cover-image.jpg"
}
```

### Delete
`/api/books/{id}` - soft-deletes from database a record with the specified id, accessible for role Admin.

**Exemple link:**
`http://localhost:8080/api/books/1`

**Response status code**: 200

## 📜 Category 

### Post
`/api/categories` - *creates a new category, accessible for role Admin.*

**Exemple link:**
`http://localhost:8080/api/categories`

**Exemple request:**
```json
{
    "name": "Advantures",
    "description": "book about advantures"
}
```
Name must not be blank, description may be null or empty.

**Response status code**: 201

Exemplary response:
```json
{
    "id": "1",
    "name": "Advantures",
    "description": "book about advantures"
}
```

### Get
`/api/categories` - *returns a list of all categories from database.*

**Exemple link:**
`http://localhost:8080/api/categories`

**Response status code**: 200

**Exemple response:**
```json
[
    {
        "id": 1,
        "name": "Advantures",
        "description": "book about advantures"
    },
    {
        "id": 2,
        "name": "History",
        "description": "Book described some period of history"
    }
]
```
---

`/api/categories/{id}` - *returns category that has specified id value.*

**Exemple link:**
`http://localhost:8080/api/categories/1`

**Response status code**: 200

Exemple response:
```json
{
    "id": 1,
    "name": "Advantures",
    "description": "book about advantures"
}
```
---

`/api/categories/{id}/books` - *returns a list of books, that have some category*

**Exemple link:**
`http://localhost:8080/api/categories/1/books`

**Response status code**: 200

**Exemple response:**
```json
[
    {
    "id": 1,
    "title": "White Fang",
    "author": "Jack London",
    "isbn": "00000000000001",
    "price": 19.90,
    "description": "Book about adventure",
    "coverImage": "http://example.com/whiteFang.jpg",
    },
    {
    "id": 3,
    "title": "Shantaram",
    "author": "Greg David Roberts",
    "isbn": "00000000000003",
    "price": 27.90,
    "description": "Book about adventure",
    "coverImage": "https://example.com/shantaram-cover-image.jpg"
    }
]
```
### Put
`http://localhost:8080/api/categories/{id}` - *updates category with specified id, accessible for role Admin.*

**Exemple link:**
`http://localhost:8080/api/categories/2`

**Exemplary request:**
```json
{
    "name": "updated category",
    "description": "updated description"
}
```
**Response status code**: 202

**Exempe response:**
```json
{
    "id": "2",
    "name": "updated category",
    "description": "updated description"
}
```

### Delete
`http://localhost:8080/api/categories/{id}` - *soft-deletes category with specified id, accessible for role Admin.*

**Exemple link:**
`http://localhost:8080/api/categories/2`

**Response status code**: 200




##  ❓ Challenges and Solutions 💪

   The most difficult task for me in this project was to bring all the components together and combine them into a single functioning mechanism. However, thanks to the systematic work and constant support of the mentors, it was still possible to implement this project.

   Despite the fact that this kind of project was the first in my practice, I must admit that the process of creating this project was very interesting and instructive, as it allowed me to consolidate the learned theory in practice. I am looking forward to the next projects to be able to improve my skills and deepen my knowledge.

## 🌼 Possible improvements

**Here are some suggestions for future improvements to the website:**

👍 **Improved user experience:** *Constantly improving the interface and user experience to make browsing, searching and buying books even more intuitive and pleasant for users. This may include conducting user surveys or usability testing to gather feedback and identify areas for improvement.*

😃 **Personalized features:** *Implement personalized recommendations based on users' browsing history, reading preferences and past purchases. This can improve the user experience by helping users discover new books that match their interests.*

📱  **Mobile Optimization:** *Ensure that the website is fully optimized for mobile devices to satisfy users who prefer to view on smartphones or tablets. Responsive design and mobile-optimized features can dramatically improve accessibility and engagement.*

‍🤝‍🧑 **Social Integration:** *Integrate social media features to allow users to share their favorite books, reviews and recommendations with their friends and followers. This can help increase website visibility and attract new users through referrals within your circle.*

👨‍👩‍👦 **Community Engagement:** *Create a sense of community among users by including features such as book clubs, discussion forums, or Q&A sessions with authors. Creating opportunities for interaction and collaboration can improve user retention and loyalty.*

📚 **Expanded catalog:** *Continually expand and diversify the book catalog to meet a wider range of interests and tastes. Regularly adding new titles, genres and authors can keep users interested and encourage repeat visits to the website.*

🚚 **Checkout Streamline:** *Streamline the checkout process and reduce third-party points to minimize cart abandonment. Implement features such as quick purchase, one-time purchase and different payment options to make the purchase process as convenient and easy as possible.*

💪 **Performance Optimization:** *Continuously monitor and optimize website performance to ensure fast loading, smooth.*




 
