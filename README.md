# TP 4 - CQRS et Event Sourcing

Auteur : OUAZZANI TAYBI Ilyas
Enseignant : Mohamed YOUSSFI

Implémenter un compte bancaire avec Axon, séparation commandes/lectures et historique événementiel.

## Command Side
AccountAggregate est identifié par @AggregateIdentifier. Les commandes Create, Activate, Credit, Debit et Status portent @TargetAggregateIdentifier. Les CommandHandlers vérifient les règles métier, puis publient des événements via AggregateLifecycle.apply(). Un débit supérieur au solde ou une opération sur un compte inactif est refusé.

## Event Sourcing
Les événements Created, Activated, Credited, Debited et StatusUpdated décrivent les changements. Les EventSourcingHandlers reconstruisent l’état interne par application successive des événements. Les contrôles métier appartiennent aux handlers de commandes ; les handlers d’évolution appliquent seulement les faits déjà acceptés.

## Query Side
La projection écoute les événements dans le groupe projections. Elle met à jour les entités JPA Account et AccountOperation. Le QueryGateway expose All et Statement ; le relevé contient le compte et ses opérations chronologiques. Cette base de lecture est distincte du modèle de décision de l’agrégat.

## SSE et abonnement
La requête Watch est un abonnement Axon. Son résultat initial fournit l’historique ; QueryUpdateEmitter transmet ensuite chaque nouvelle opération. Le contrôleur renvoie un Flux et ferme l’abonnement à la déconnexion. Le navigateur ou un client SSE peut suivre /accounts/{id}/watch.

## Persistance et distribution
Le profil par défaut utilise H2 et désactive Axon Server pour les tests autonomes. Le profil postgres utilise PostgreSQL ; le profil distributed active Axon Server via gRPC (8124). Le fichier Compose fournit PostgreSQL, PgAdmin et Axon Server avec volumes persistants. La console Axon est disponible sur 8024.

## Environnement Java
Le code est compatible Java 17, version installée sur la machine de préparation. La consigne utilise Java 21 : pour exécuter exactement dans cet environnement, installer le JDK 21 et lancer Maven avec -Djava.version=21. Aucune API spécifique à Java 21 n’est nécessaire dans cette implémentation.

## Exécution
```powershell
mvn clean test
# Mode autonome
mvn spring-boot:run
# Mode PostgreSQL + Axon Server
docker compose up -d
mvn spring-boot:run `
 "-Dspring-boot.run.profiles=postgres,distributed" `
 -Djava.version=21
# http://localhost:8086/swagger-ui.html
$a = Invoke-RestMethod http://localhost:8086/accounts `
 -Method Post -ContentType application/json -Body '{"balance":100}'
Invoke-RestMethod "http://localhost:8086/accounts/$($a.id)/activate" `
 -Method Post
```

## Validation
ma.ilyas.AccountAggregateTest : 4 test(s), 0 échec(s), 0 erreur(s).
ma.ilyas.ProjectionIntegrationTest : 1 test(s), 0 échec(s), 0 erreur(s).

Exécution réelle vérifiée en mode autonome puis avec PostgreSQL et Axon Server : solde final 115, deux opérations projetées et quatre événements dans l’Event Store. Le refus de débit excessif retourne HTTP 409.
