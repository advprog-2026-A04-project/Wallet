Microservice deployment link: spotty-brandea-adpro12903i32103erj2-5dede6a5.koyeb.app

API list:

| Method | Endpoint                             | Description                                |
|--------|--------------------------------------|--------------------------------------------|
| POST   | `/wallet/balance`                    | Check balance                              |
| POST   | `/wallet/transactions`               | Fetch transactions                         |
| POST   | `/wallet/topup`                      | Create top-up request                      |
| POST   | `/wallet/topup/{id}/mark-success`    | Mark top-up request as successful          |
| POST   | `/wallet/topup/{id}/mark-failed`     | Mark top-up request as failed              |
| POST   | `/wallet/withdraw`                   | Create withdrawal request (Jastipers only) |
| POST   | `/wallet/withdraw/{id}/mark-success` | Mark withdrawal request as successful      |
| POST   | `/wallet/withdraw/{id}/mark-failed`  | Mark withdrawal request as failed          |
| POST   | `/wallet/deduct`                     | Deduct from balance                        |
| POST   | `/wallet/refund`                     | Refund to balance                          |
| GET    | `/app/index.html`                    | Barebones web app for wallet microservice  |
