package ma.ilyas;
import org.springframework.stereotype.Component;import org.axonframework.config.ProcessingGroup;import org.axonframework.eventhandling.EventHandler;import org.axonframework.queryhandling.*;import org.springframework.transaction.annotation.Transactional;import static ma.ilyas.Messages.*;import java.util.List;
@Component @ProcessingGroup("projections") @Transactional public class Projection {
private final AccountRepository accounts;private final OperationRepository operations;private final QueryUpdateEmitter emitter;
public Projection(AccountRepository a,OperationRepository o,QueryUpdateEmitter e){accounts=a;operations=o;emitter=e;}
@EventHandler public void on(Created e){accounts.save(new Account(e.id(),e.balance(),"CREATED"));}
@EventHandler public void on(Activated e){Account a=accounts.findById(e.id()).orElseThrow();a.status="ACTIVE";accounts.save(a);}
@EventHandler public void on(StatusUpdated e){Account a=accounts.findById(e.id()).orElseThrow();a.status=e.status();accounts.save(a);}
@EventHandler public void on(Credited e){change(e.id(),e.amount(),"CREDIT");}
@EventHandler public void on(Debited e){change(e.id(),e.amount(),"DEBIT");}
private void change(String id,java.math.BigDecimal x,String type){Account a=accounts.findById(id).orElseThrow();a.balance=type.equals("CREDIT")?a.balance.add(x):a.balance.subtract(x);accounts.save(a);AccountOperation o=operations.save(new AccountOperation(id,type,x));emitter.emit(Watch.class,q->q.id().equals(id),view(o));}
private OperationView view(AccountOperation o){return new OperationView(o.id,o.accountId,o.type,o.amount,o.date);}
private AccountView view(Account a){return new AccountView(a.id,a.balance,a.status);}
@QueryHandler public List<AccountView> all(All q){return accounts.findAll().stream().map(this::view).toList();}
@QueryHandler public StatementView statement(Statement q){return new StatementView(view(accounts.findById(q.id()).orElseThrow()),operations.findByAccountIdOrderByDateAsc(q.id()).stream().map(this::view).toList());}
@QueryHandler public List<OperationView> watch(Watch q){return operations.findByAccountIdOrderByDateAsc(q.id()).stream().map(this::view).toList();}}
