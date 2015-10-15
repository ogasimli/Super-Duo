package org.ogasimli.footballscores.api;

import com.google.gson.Gson;

/**
 * Created by com.ogasimli on 11.10.2015.
 */
public class GetTeamInformationResponse {

    public String name;
    public String code;
    public String shortName;
    public String squadMarketValue;
    public String crestUrl;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
