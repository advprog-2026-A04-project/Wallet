# Wallet Service

Wallet service for Milestone `25%` and `50%`.

## Deployed URL

- `https://wallet-api-383620816191.us-central1.run.app`

## Implemented Scope

- `POST /wallet/balance`
- `POST /wallet/transactions`
- `POST /wallet/topup`
- `POST /wallet/topup/{id}/mark-success`
- `POST /wallet/topup/{id}/mark-failed`
- `POST /wallet/withdraw`
- `POST /wallet/withdraw/{id}/mark-success`
- `POST /wallet/withdraw/{id}/mark-failed`
- `POST /wallet/deduct`
- `POST /wallet/refund`

JWT auth is used for end-user wallet actions. `X-Internal-Token` is accepted for Order-to-Wallet balance, deduct, and refund calls.

## Local Run

Prerequisites:
- Java `21`

Run:

```bash
./gradlew bootRun
```

PowerShell:

```powershell
.\gradlew.bat bootRun
```

Default local URL:
- `http://localhost:8080`

## Environment Variables

- `PORT`
- `DB_URL`
- `DB_DRIVER`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`
- `JWT_SECRET`
- `INTERNAL_API_TOKEN`

Defaults are configured for an H2 file database under `/tmp`.

## Test

```bash
./gradlew test
```

Includes:
- wallet flow integration test
- concurrent deduct coverage to verify balance never drops below zero

## Cloud Run Deploy

```bash
gcloud run deploy wallet-api --source . --region us-central1 --allow-unauthenticated --max-instances=1 \
  --set-env-vars APP_CORS_ALLOWED_ORIGINS=https://advprog-frontend-m25-m50-383620816191.us-central1.run.app \
  --set-env-vars JWT_SECRET=<shared-jwt-secret> \
  --set-env-vars INTERNAL_API_TOKEN=<shared-internal-token>
```

## Notes

- Top-up remains a two-step flow for demoability: create request, then mark success.
- Deduct and refund are idempotent per `(userId, orderId)`.
