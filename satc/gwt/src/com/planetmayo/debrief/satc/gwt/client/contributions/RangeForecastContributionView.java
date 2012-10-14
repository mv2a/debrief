package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public class RangeForecastContributionView extends Composite implements ContributionData {

	private static RangeForecastContributionViewUiBinder uiBinder = GWT
			.create(RangeForecastContributionViewUiBinder.class);

	interface RangeForecastContributionViewUiBinder extends
			UiBinder<Widget, RangeForecastContributionView> {
	}

	public RangeForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@Override
	public void setData(BaseContribution contribution) {
		// TODO Auto-generated method stub
		
	}

}
