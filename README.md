# Project Title

## Objectives

1. **Implement a robust data processing pipeline** that can handle large CSV files efficiently.
2. **Provide a clean REST API** for querying processed data, using Spring Boot 3.x.
3. **Ensure comprehensive unit and integration tests** with JUnit 5 and Mockito.
4. **Document the build and deployment steps** clearly for CI/CD pipelines.


## Overview

This project presents a **conceptual simulator** to evaluate **Maximal Extractable Value (MEV)** concentration under different validator stake distributions in **post-Merge Ethereum (Proof of Stake)**.

Ethereum’s transition from Proof of Work to Proof of Stake has amplified the role of validator stake concentration in determining block proposal probability and MEV extraction. Validators controlling large amounts of stake can increase their likelihood of proposing blocks by operating multiple validators, leading to **probability amplification** and **disproportionate MEV rewards**.

This simulator investigates a **stake normalization approach**, where each entity’s effective stake is capped, to conceptually analyze whether such normalization can reduce unfair MEV advantages and mitigate centralization risks at the **consensus layer**.

> ⚠️ **Important Note**  
> This is **not** a full Ethereum execution engine.  
> It is a **probabilistic abstraction** designed for research and academic evaluation of consensus-layer fairness.

---

## Research Motivation

Most existing MEV mitigation techniques focus on:
- Transaction ordering fairness
- Proposer–Builder Separation (PBS)
- Cryptographic privacy mechanisms

However, they largely **ignore consensus-layer economic fairness**, specifically:
- How stake distribution affects block proposal probability
- How validator multiplication amplifies MEV extraction

This project addresses that gap by simulating:
- Validator stake splitting
- MEV extraction probability
- Stake normalization effects

---

## Key Features

- Simulates **100 entities**:
  - 5 whales
  - 25 mid-size investors
  - 70 small investors
- Models **validator splitting** (32 ETH per validator)
- Simulates **100,000 transactions**
- Probabilistic block proposer selection
- MEV extraction modeling
- Stake normalization with configurable caps
- Before-and-after MEV comparison

---

## How the Simulator Works

1. **Entities** are created with different stake sizes
2. Stake is split into multiple validators (32 ETH each)
3. Block proposers are selected probabilistically based on stake
4. MEV is extracted from a subset of transactions
5. Total MEV earned by each entity is recorded
6. Stake normalization is applied (stake cap per entity)
7. The simulation is rerun and results are compared

---

## Requirements

- Java **JDK 17 or higher**
- Apache Maven
- Docker
- Windows, macOS, or Linux

## Build and Run the Project

First, start Ganache:
```bash
docker-compose up -d
```

Compile the project:
```bash
mvn clean compile
```

Run the simulator:
```bash
mvn spring-boot:run
```

Run tests:
```bash
mvn test
```



Sample Output

Below is an example output produced by the simulator:

=== Before Stake Normalization ===
Whale_0 | Stake: 3200 | MEV Earned: 1184.52
Whale_1 | Stake: 3200 | MEV Earned: 1211.33
Whale_2 | Stake: 3200 | MEV Earned: 1198.41
Mid_0   | Stake: 800  | MEV Earned: 312.87
Small_0| Stake: 100  | MEV Earned: 12.11
...

=== After Stake Normalization ===
Whale_0 | Stake: 1600 | MEV Earned: 602.18
Whale_1 | Stake: 1600 | MEV Earned: 590.71
Whale_2 | Stake: 1600 | MEV Earned: 611.02
Mid_0   | Stake: 800  | MEV Earned: 314.55
Small_0| Stake: 100  | MEV Earned: 13.02
...



Interpretation of Results

Before normalization, whale entities earn MEV disproportionate to their stake due to validator multiplication.

After normalization, MEV extraction by whales is significantly reduced.

Smaller and mid-size entities experience relatively fairer MEV distribution.

This demonstrates that stake normalization can conceptually reduce consensus-layer unfairness.

Academic Disclaimer

This simulator is intended for research and educational purposes only.
It abstracts away execution-layer complexity and does not reflect full Ethereum protocol behavior.

The goal is to isolate and evaluate consensus-layer economic effects related to stake distribution and MEV extraction.
Author:
Hareer Shakeel
