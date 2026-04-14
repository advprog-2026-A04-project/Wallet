Microservice deployment link: spotty-brandea-adpro12903i32103erj2-5dede6a5.koyeb.app

# How to deploy
```bash
./gradlew bootRun
```
# APIs

Note: `{id}` is a URL parameter.

| Method | Endpoint                             | Request body                      | Description                                |
|--------|--------------------------------------|-----------------------------------|--------------------------------------------|
| POST   | `/wallet/balance`                    | `userId`                          | Check balance                              |
| POST   | `/wallet/transactions`               | -                                 | Fetch transactions                         |
| POST   | `/wallet/topup`                      | `userId`, `amount`                | Create top-up request                      |
| POST   | `/wallet/topup/{id}/mark-success`    | -                                 | Mark top-up request as successful          |
| POST   | `/wallet/topup/{id}/mark-failed`     | -                                 | Mark top-up request as failed              |
| POST   | `/wallet/withdraw`                   | `userId`, `amount`, `destination` | Create withdrawal request (Jastipers only) |
| POST   | `/wallet/withdraw/{id}/mark-success` | -                                 | Mark withdrawal request as successful      |
| POST   | `/wallet/withdraw/{id}/mark-failed`  | -                                 | Mark withdrawal request as failed          |
| POST   | `/wallet/deduct`                     | `userId`, `amount`, `orderId`     | Deduct from balance as payment for order   |
| POST   | `/wallet/refund`                     | `userId`, `amount`, `orderId`     | Refund to balance                          |
| GET    | `/app/index.html`                    | N/A                               | Barebones web app for wallet microservice  |

## Examples

* `POST  /wallet/balance`
  ```json
  {
    "userId": 123456
  }
  ```
* `POST  /wallet/transactions`
* `POST  /wallet/topup`
  ```json
  {
    "userId": 123456,
    "amount": 1000000.0
  }
  ```
* `POST  /wallet/topup/111/mark-success`
* `POST  /wallet/topup/111/mark-failed`
* `POST  /wallet/withdraw`
  ```json
  {
    "userId": 123456,
    "amount": 1000000.0,
    "destination": "burhanpay"
  }
  ```
* `POST  /wallet/withdraw/111/mark-success`
* `POST  /wallet/withdraw/111/mark-failed`
* `POST  /wallet/deduct`
  ```json
  {
    "userId": 123456,
    "amount": 1000000.0,
    "orderId": 111
  }
  ```
* `POST  /wallet/refund`
  ```json
  {
    "userId": 123456,
    "amount": 1000000.0,
    "orderId": 111
  }
  ```
