# Event-Feedback-Analyzer
Event Feedback Analyzer helps teams gather and interpret participant opinions after workshops, hackathons, or conferences.
Each piece of feedback is classified as Positive, Neutral, or Negative, providing clear insight into event sentiment.

# Features

- **Event Management:**
  - Create new events with title and description.
  - Retrieve all existing events or a single event by ID.
  - Automatically link each event to submitted feedback entries.
  - Supports JSON-based REST communication (HTTP 200, HTTP 201, HTTP 404).
 
- **Feedback Collection and Sentiment Analysis:**
  - Accepts written feedback for any event through /api/feedback.
  - Automatically analyzes the text using Hugging Face’s RoBERTa model.
  - Classifies feedback sentiment as POSITIVE, NEUTRAL, NEGATIVE.
  - Returns structured JSON response (HTTP 201 on success, HTTP 400 on invalid input).
 
- **OpenAPI Documentation**
  - All endpoints are documented using OpenAPI/Swagger.
  - Auto-generated API docs are available at `/v3/api-docs`.
 
- **Extra(bonus)**
  - Implemented a simple UI using React for creating events, submiting events, and displaying the event sentiment summary.
  - Deployed the project in [Google Cloud](https://event-feedback-analyzer-frontend-react1-328172629172.europe-west1.run.app)

# Getting Started
Ensure Docker and Docker Compose are installed and running on your machine.

## HuggingFace API Token Configuration
This project uses a public RoBERTa-based sentiment analysis model hosted on Hugging Face. <br>
To access the API, you need to configure a free Hugging Face access token.

### Steps:

1. Create or sign in https://huggingface.co/ account
2. Once logged in, navigate to **Settings** and press **Access Tokens**.
3. Generate a new token with **Read** permissions.
4. Add it to main/src/resources/application.yaml environment file


   ```
    HF_API_URL: https://router.huggingface.co/hf-inference/models/cardiffnlp/twitter-roberta-base-sentiment-latest
    HF_API_TOKEN: your_api_token
   ```

## SpringBoot Configuration

### 1. Clone the Repository

```
git clone https://github.com/Pijus-B/Event-Feedback-Analyzer.git
cd Event-Feedback-Analyzer
```

### 2. Start Docker containers

```
make up-build
```
