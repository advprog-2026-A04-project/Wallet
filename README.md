Microservice deployment link: spotty-brandea-adpro12903i32103erj2-5dede6a5.koyeb.app

API list:

| Endpoint                             | Description                                |
|--------------------------------------|--------------------------------------------|
| `/wallet/balance`                    | Check balance                              |
| `/wallet/transactions`               | Fetch transactions                         |
| `/wallet/topup`                      | Create top-up request                      |
| `/wallet/topup/{id}/mark-success`    | Mark top-up request as successful          |
| `/wallet/topup/{id}/mark-failed`     | Mark top-up request as failed              |
| `/wallet/withdraw`                   | Create withdrawal request (Jastipers only) |
| `/wallet/withdraw/{id}/mark-success` | Mark withdrawal request as successful      |
| `/wallet/withdraw/{id}/mark-failed`  | Mark withdrawal request as failed          |
| `/wallet/deduct`                     | Deduct from balance                        |
| `/wallet/refund`                     | Refund to balance                          |
| `/app/index.html`                    | Barebones web app for wallet microservice  |
