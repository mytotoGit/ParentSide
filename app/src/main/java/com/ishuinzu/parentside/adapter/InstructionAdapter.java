package com.ishuinzu.parentside.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.object.Instruction;

import java.util.List;

public class InstructionAdapter extends PagerAdapter {
    private Context context;
    private List<Instruction> instructions;
    private LayoutInflater inflater;

    public InstructionAdapter(Context context, List<Instruction> instructions) {
        this.context = context;
        this.instructions = instructions;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return instructions.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (MaterialCardView) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_instruction, container, false);

        LottieAnimationView lottieInstruction = view.findViewById(R.id.lottieInstruction);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtDescription = view.findViewById(R.id.txtDescription);

        lottieInstruction.setAnimation(instructions.get(position).getImg_id());
        txtTitle.setText(instructions.get(position).getTitle());
        txtDescription.setText(instructions.get(position).getDescription());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((MaterialCardView) object);
    }
}
