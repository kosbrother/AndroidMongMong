package com.kosbrother.mongmongwoo.campaignrules;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.databinding.ActivityCampaignRulesBinding;
import com.kosbrother.mongmongwoo.entity.camapign.CampaignRuleEntity;

import java.util.ArrayList;
import java.util.List;

public class CampaignRulesActivity extends BaseActivity implements DataManager.ApiCallBack {

    private ActivityCampaignRulesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_campaign_rules);
        setToolbar();

        binding.setShowLoading(true);
        DataManager.getInstance().getCampaignRules(this);
    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onError(String errorMessage) {
        binding.setShowLoading(false);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object data) {
        List<CampaignRuleEntity> campaignRuleEntities = (List<CampaignRuleEntity>) data;

        ArrayList<CampaignRuleViewModel> campaignRuleViewModels = new ArrayList<>();
        for (CampaignRuleEntity campaignRule : campaignRuleEntities) {
            campaignRuleViewModels.add(new CampaignRuleViewModel(campaignRule));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_campaign_rules_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CampaignRulesAdapter(campaignRuleViewModels, this));
        binding.setShowLoading(false);
    }

    public void onCampaignRuleClick(int id, String title) {
        Intent intent = new Intent(this, CampaignRuleDetailActivity.class);
        intent.putExtra(CampaignRuleDetailActivity.EXTRA_INT_ID, id);
        intent.putExtra(CampaignRuleDetailActivity.EXTRA_STRING_TITLE, title);
        startActivity(intent);
    }
}
