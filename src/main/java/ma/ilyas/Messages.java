package ma.ilyas;
import org.axonframework.modelling.command.TargetAggregateIdentifier;import java.math.BigDecimal;import java.util.List;
public class Messages {
public record Create(@TargetAggregateIdentifier String id,BigDecimal balance){}
public record Activate(@TargetAggregateIdentifier String id){}
public record Credit(@TargetAggregateIdentifier String id,BigDecimal amount){}
public record Debit(@TargetAggregateIdentifier String id,BigDecimal amount){}
public record Status(@TargetAggregateIdentifier String id,String status){}
public record Created(String id,BigDecimal balance){}
public record Activated(String id){}
public record Credited(String id,BigDecimal amount){}
public record Debited(String id,BigDecimal amount){}
public record StatusUpdated(String id,String status){}
public record All(){}
public record Statement(String id){}
public record Watch(String id){}
public record OperationView(String id,String accountId,String type,BigDecimal amount,String date){}
public record AccountView(String id,BigDecimal balance,String status){}
public record StatementView(AccountView account,List<OperationView> operations){}
}
