package ma.ilyas;
import org.springframework.web.bind.annotation.*;import org.axonframework.commandhandling.gateway.CommandGateway;import org.axonframework.queryhandling.*;import org.axonframework.messaging.responsetypes.ResponseTypes;import reactor.core.publisher.Flux;import java.util.*;import java.math.BigDecimal;import static ma.ilyas.Messages.*;
@RestController @RequestMapping("/accounts") public class AccountController {
private final CommandGateway commands;private final QueryGateway queries;public AccountController(CommandGateway c,QueryGateway q){commands=c;queries=q;}
public record Opening(BigDecimal balance){} public record Amount(BigDecimal amount){} public record NewStatus(String status){}
@PostMapping public Map<String,String> create(@RequestBody Opening x){String id=UUID.randomUUID().toString();commands.sendAndWait(new Create(id,x.balance()));return Map.of("id",id);}
@PostMapping("/{id}/activate") public void activate(@PathVariable String id){commands.sendAndWait(new Activate(id));}
@PostMapping("/{id}/credit") public void credit(@PathVariable String id,@RequestBody Amount x){commands.sendAndWait(new Credit(id,x.amount()));}
@PostMapping("/{id}/debit") public void debit(@PathVariable String id,@RequestBody Amount x){commands.sendAndWait(new Debit(id,x.amount()));}
@PutMapping("/{id}/status") public void status(@PathVariable String id,@RequestBody NewStatus x){commands.sendAndWait(new Status(id,x.status()));}
@GetMapping public java.util.concurrent.CompletableFuture<List<AccountView>> all(){return queries.query(new All(),ResponseTypes.multipleInstancesOf(AccountView.class));}
@GetMapping("/{id}/statement") public java.util.concurrent.CompletableFuture<StatementView> statement(@PathVariable String id){return queries.query(new Statement(id),StatementView.class);}
@GetMapping(value="/{id}/watch",produces="text/event-stream") public Flux<OperationView> watch(@PathVariable String id){return Flux.defer(()->{var result=queries.subscriptionQuery(new Watch(id),ResponseTypes.multipleInstancesOf(OperationView.class),ResponseTypes.instanceOf(OperationView.class));return result.initialResult().flatMapMany(Flux::fromIterable).concatWith(result.updates()).doFinally(s->result.close());});}}
