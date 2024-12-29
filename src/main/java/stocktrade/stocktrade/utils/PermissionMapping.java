package stocktrade.stocktrade.utils;

import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.exceptions.ResourceNotFound;

import java.lang.reflect.Array;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static stocktrade.stocktrade.enums.Permissions.*;

public class PermissionMapping {
    public Permissions getSuperPermission(Set<Permissions> permissions){
        if(permissions.size()==1){
            return permissions.stream().findFirst().orElseThrow(()->new ResourceNotFound("No permissions found"));
        }
        boolean canCreatePlan = permissions.contains(CREATE_PLAN);
        boolean canUpdatePlan = permissions.contains(UPDATE_PLAN);
        boolean canDeletePlan = permissions.contains(DELETE_PLAN);

        if(canCreatePlan && canUpdatePlan && canDeletePlan){
            return PLAN_MASTER;
        }
        else if(canCreatePlan && canUpdatePlan){
            return PLAN_CREATE_UPDATE;
        }
        else if(canCreatePlan && canDeletePlan){
            return PLAN_CREATE_DELETE;
        }
        else if(canUpdatePlan && canDeletePlan){
            return PLAN_UPDATE_DELETE;
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
