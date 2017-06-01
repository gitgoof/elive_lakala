package com.lakala.shoudan.activity.myaccount;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.lakala.shoudan.R;

/**
 * 地区dialog
 * @author zmy
 *
 */
public class SpinnerDialog extends AlertDialog.Builder{
	//地区的最终结果，选择后的
    private AreaInfo selectionRegion;

    private int provinceIndex;

    private int cityIndex;

    public AreaInfo getSelectionRegion() {
        return selectionRegion;
    }

    public SpinnerDialog(Context context, List<Region> regions) {
        super(context);

        init(context, regions);
    }

    private void init(final Context context,final List<Region> regions){
        setTitle("请点击选择");

        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.spinner_dialog, null);
        Spinner province = (Spinner) linearLayout.findViewById(R.id.province);
        province.setAdapter(new RegionAdapter(context, regions));
        province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Region region = (Region) adapterView.getItemAtPosition(i);
                selectionRegion = new AreaInfo();
                selectionRegion.setName(region.getName());
                selectionRegion.setCode(region.getCode());
                provinceIndex = i;
                Spinner city = (Spinner) linearLayout.findViewById(R.id.city);
                city.setAdapter(new RegionAdapter(context, regions.get(provinceIndex).getChildren()));
                city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Region itemAtPosition = (Region) adapterView.getItemAtPosition(i);

                        selectionRegion.getChild().setCode(itemAtPosition.getCode());
                        selectionRegion.getChild().setName(itemAtPosition.getName());
                        Spinner region = (Spinner) linearLayout.findViewById(R.id.region);
                        cityIndex = i;
                        //修改没有第三项地区时送得内容
                        if(regions.get(provinceIndex).getChildren().get(cityIndex).getChildren() == null
                        		|| regions.get(provinceIndex).getChildren().get(cityIndex).getChildren().size()==0){
                        	//没有第三项内容，设置第三项内容未空
                        	  selectionRegion.getChild().getChild().setCode("");
                              selectionRegion.getChild().getChild().setName("");
                        }
                        region.setAdapter(new RegionAdapter(context, regions.get(provinceIndex).getChildren().get(cityIndex).getChildren()));
                        region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }

                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                Region region = (Region) adapterView.getItemAtPosition(i);

                                selectionRegion.getChild().getChild().setCode(region.getCode());
                                selectionRegion.getChild().getChild().setName(region.getName());

                            }
                        }
                        );

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setView(linearLayout);

    }
}
