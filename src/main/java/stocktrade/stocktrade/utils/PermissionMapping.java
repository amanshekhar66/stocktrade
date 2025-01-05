package stocktrade.stocktrade.utils;

import lombok.NoArgsConstructor;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.exceptions.ResourceNotFound;

import java.util.Set;
import static stocktrade.stocktrade.enums.Permissions.*;
@NoArgsConstructor
public class PermissionMapping {
    public Permissions getSuperPermission(Set<Permissions> permissions){
        if(permissions.size()==1){
            return permissions.stream().findFirst().orElseThrow(()->new ResourceNotFound("No permissions found"));
        }
        boolean canCreatePlan = permissions.contains(CREATE_PLAN);
        boolean canUpdatePlan = permissions.contains(UPDATE_PLAN);
        boolean canDeletePlan = permissions.contains(DELETE_PLAN);
        boolean canAddCustomer = permissions.contains(ADD_CUSTOMER_TO_PLAN);

        if(canUpdatePlan && canDeletePlan){
            return PLAN_UPDATE_DELETE;
        }
        if(canUpdatePlan && canAddCustomer){
            return PLAN_UPDATE_ADD_CUSTOMER;
        }
        if(canDeletePlan && canAddCustomer){
            return PLAN_DELETE_ADD_CUSTOMER;
        }

        boolean canCreateRecommendation = permissions.contains(CREATE_RECOMMENDATION);
        boolean canUpdateRecommendation = permissions.contains(UPDATE_RECOMMENDATION);
        boolean canDeleteRecommendation= permissions.contains(DELETE_RECOMMENDATION);

        if(canCreateRecommendation && canUpdateRecommendation && canDeleteRecommendation){
            return RECOMMENDATION_MASTER;
        }
        else if(canCreateRecommendation && canUpdateRecommendation){
            return RECOMMENDATION_CREATE_UPDATE;
        }
        else if(canCreateRecommendation && canDeleteRecommendation){
            return RECOMMENDATION_CREATE_DELETE;
        }
        else if(canUpdateRecommendation && canDeleteRecommendation){
            return RECOMMENDATION_UPDATE_DELETE;
        }
        return null;
    }

}
