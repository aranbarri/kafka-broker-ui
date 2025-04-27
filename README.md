# Apache Kafka (KRaft Mode) + Kafka-UI 

This project sets up a local environment of **Apache Kafka** (without Zookeeper) along with **Kafka-UI** for visual management of brokers, topics, producers, and consumers.

## 📦 Included Services

- **Kafka Broker** (Bitnami, KRaft mode)
- **Kafka-UI** (Web dashboard to monitor Kafka)

---

## 🚀 How to Start the Environment

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/kafka-kraft-ui.git
cd kafka-kraft-ui
```

### 2. Prepare Permissions and Volume
```bash
docker-compose down -v
rm -rf ./data/kafka
mkdir -p ./data/kafka
sudo chown -R 1001:1001 ./data/kafka
```

### 3. Launch Services
```bash
docker-compose up -d
```

### 4. Access Kafka-UI
Open your browser at: [http://localhost:8080](http://localhost:8080)

You should see the `local-kraft` cluster connected.

---

## ⚙️ Configuration

- **Kafka Broker**:  
  - Host: `localhost`
  - Port: `9092`
  - Advertised Listener pointing to Docker gateway (`172.17.0.1`).

- **Kafka-UI**:  
  - Accessible at `http://localhost:8080`.
  - Automatically connected to the broker via `extra_hosts`.

---

## 🛑 Stop Services

```bash
docker-compose down
```

To also remove Kafka data:

```bash
docker-compose down -v
rm -rf ./data/kafka
```

---

## 🎯 Requirements

- Docker
- Docker Compose
- Permission to modify folder ownership (`sudo`)

---

## 📖 Useful Resources

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kafka-UI GitHub](https://github.com/provectus/kafka-ui)
- [Bitnami Kafka Docker](https://hub.docker.com/r/bitnami/kafka)

---
