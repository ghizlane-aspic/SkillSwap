# 🔄 SkillSwap

**"Le savoir ne vaut que s'il est partagé par tous"**

SkillSwap est une plateforme collaborative d'échange de compétences basée sur le concept de **Crédit Temps**. Les utilisateurs échangent leurs savoirs sans transaction financière.

## 🛠️ Technologies

- **JDK 21**
- **Jakarta EE 10** (JSF 4.0, CDI 4.0, Servlet 6.0)
- **Hibernate 6.4** (JPA 3.1)
- **MySQL 8**
- **Maven**
- **WildFly / GlassFish** (serveur d'application Jakarta EE 10)

## 📋 Prérequis

- JDK 21+
- Maven 3.9+
- MySQL 8+
- Serveur Jakarta EE 10 compatible (WildFly 30+, GlassFish 7+)

## 🚀 Installation

1. **Cloner le projet**
   ```bash
   git clone https://github.com/votre-user/SkillSwap.git
   cd SkillSwap


## 🗄️ Base de données

Créer la base de données MySQL :

```sql
CREATE DATABASE skillswap;
````

Configurer la connexion dans :

```
src/main/resources/META-INF/persistence.xml
```

Compiler le projet :

```bash
²
```

Déployer le fichier suivant sur votre serveur :

```
target/SkillSwap-1.0-SNAPSHOT.war
```

---

## 📦 Architecture

```
MVC (Model-View-Controller)
├── Model      → JPA Entities (Hibernate)
├── View       → JSF Pages (.xhtml)
├── Controller → CDI Managed Beans
├── DAO        → Data Access Objects
└── Service    → Business Logic
```

---

## 👥 Équipe

* HASSOUN Aya
* TOUGUI Ghizlane

**Encadré par :** Mme BEL MOKADEM Houda
**Établissement :** ENSA Tanger
**Module :** Développement Web Java (JSF / Hibernate)
