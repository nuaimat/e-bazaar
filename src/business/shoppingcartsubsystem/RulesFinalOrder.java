package business.shoppingcartsubsystem;

import java.util.HashMap;
import java.util.List;

import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesSubsystem;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.rulesbeans.FinalOrderBean;
import business.rulesbeans.ShopCartBean;
import business.rulesubsystem.RulesSubsystemFacade;

/**
 * This Rules class is different from RulesShoppingCart
 * (even though it checks aspects of a Shopping Cart)
 * because it needs to execute at a different time during
 * execution of the application (namely, when final order
 * is submitted)
 */

class RulesFinalOrder implements Rules {
    private RulesConfigProperties config = new RulesConfigProperties();
    private HashMap<String,DynamicBean> table;
    private DynamicBean bean;

    public RulesFinalOrder(ShoppingCartImpl liveCart) {
        bean = new FinalOrderBean(liveCart);
    }

    @Override
    public String getModuleName() {
        return config.getProperty(RulesConfigKey.FINAL_ORDER_MODULE.getVal());
    }

    @Override
    public String getRulesFile() {
        return config.getProperty(RulesConfigKey.FINAL_ORDER_RULES_FILE.getVal());
    }

    @Override
    public void prepareData() {
        table = new HashMap<String,DynamicBean>();
        String deftemplate = config.getProperty(RulesConfigKey.FINAL_ORDER_DEFTEMPLATE.getVal());
        table.put(deftemplate, bean);
    }

    @Override
    public HashMap<String, DynamicBean> getTable() {
        return table;
    }

    @Override
    public void runRules() throws BusinessException, RuleException {
        RulesSubsystem rules = new RulesSubsystemFacade();
        rules.runRules(this);
    }

    @Override
    public void populateEntities(List<String> updates) {
        // nah, nothing here but emptiness
    }

    @Override
    public List<?> getUpdates() {
        return null;
    }


}
