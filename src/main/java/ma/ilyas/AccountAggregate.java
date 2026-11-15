package ma.ilyas;
import org.axonframework.spring.stereotype.Aggregate;import org.axonframework.modelling.command.*;import org.axonframework.commandhandling.CommandHandler;import org.axonframework.eventsourcing.EventSourcingHandler;import java.math.BigDecimal;import static ma.ilyas.Messages.*;import static org.axonframework.modelling.command.AggregateLifecycle.apply;
@Aggregate public class AccountAggregate {
@AggregateIdentifier private String id;private BigDecimal balance;private String status;
protected AccountAggregate(){}
@CommandHandler public AccountAggregate(Create c){if(c.id()==null||c.id().isBlank()||c.balance()==null||c.balance().signum()<0)throw new IllegalArgumentException("Compte invalide");apply(new Created(c.id(),c.balance()));}
@CommandHandler public void on(Activate c){if(!"CREATED".equals(status))throw new IllegalStateException("Déjà activé");apply(new Activated(id));}
private void check(BigDecimal x){if(!"ACTIVE".equals(status))throw new IllegalStateException("Compte inactif");if(x==null||x.signum()<=0)throw new IllegalArgumentException("Montant positif requis");}
@CommandHandler public void on(Credit c){check(c.amount());apply(new Credited(id,c.amount()));}
@CommandHandler public void on(Debit c){check(c.amount());if(balance.compareTo(c.amount())<0)throw new IllegalStateException("Solde insuffisant");apply(new Debited(id,c.amount()));}
@CommandHandler public void on(Status c){if(!java.util.List.of("ACTIVE","SUSPENDED").contains(c.status())||"CREATED".equals(status))throw new IllegalArgumentException("Transition invalide");apply(new StatusUpdated(id,c.status()));}
@EventSourcingHandler public void on(Created e){id=e.id();balance=e.balance();status="CREATED";}
@EventSourcingHandler public void on(Activated e){status="ACTIVE";}
@EventSourcingHandler public void on(Credited e){balance=balance.add(e.amount());}
@EventSourcingHandler public void on(Debited e){balance=balance.subtract(e.amount());}
@EventSourcingHandler public void on(StatusUpdated e){status=e.status();}}
