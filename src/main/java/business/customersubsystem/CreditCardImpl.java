
package business.customersubsystem;

import business.externalinterfaces.CreditCard;


class CreditCardImpl implements CreditCard {
    String nameOnCard;
    String expirationDate;
    String cardNum;
    String cardType;
    public CreditCardImpl(String nameOnCard,String expirationDate,
               String cardNum, String cardType) {
        this.nameOnCard=nameOnCard;
        this.expirationDate=expirationDate;
        this.cardNum=cardNum;
        this.cardType=cardType;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public String getExpirationDate() {
        return expirationDate;
    }


    public String getCardNum() {
        return cardNum;
    }

 
    public String getCardType() {
        if(cardType.equals("MasterCard")){
            return "Master Card";
        }
        return cardType;
    }

}
