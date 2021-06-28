package bootcamp;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.ArrayList;
import java.util.List;

/* Our state, defining a shared fact on the ledger.

// Transaction = container, 1+ input/output states (dependent on type)

 * See src/main/java/examples/ArtState.java for an example. */

@BelongsToContract(TokenContract.class)
public class TokenState implements ContractState {
    private final Party owner;
    private final Party issuer;
    private final int amount;

    public TokenState(Party issuer, Party owner, int amount){
        this.issuer = issuer;
        this.owner = owner;
        this.amount = amount;
    }

    public List<AbstractParty> getParticipants(){
        List<AbstractParty> participants = new ArrayList<>();
        participants.add(owner);
        participants.add(issuer);
        return participants;
    }

    public Party getIssuer(){
        return issuer;
    }

    public Party getOwner(){
        return owner;
    }

    public int getAmount(){
        return amount;
    }

    // Shared facts are instance of classes implementing ContractState

}