package com.github.alkurop.updatinglist.testclasses;

import android.view.View;
import android.view.ViewGroup;

import com.github.alkurop.updatinglist.BaseLoadMoreAdapter;
import com.github.alkurop.updatinglist.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

/**
 * Created by alkurop on 01.07.16.
 */
public class TestAdapter extends BaseLoadMoreAdapter<DataObject> {
    @NotNull
    @Override
    public BaseViewHolder<DataObject> onCreateProgressVH(@NotNull ViewGroup viewGroup) {
        View v = new View(viewGroup.getContext());
        return new ProgressViewHolder(v);
    }

    @NotNull
    @Override
    public BaseViewHolder<DataObject> onCreateVH(@NotNull ViewGroup viewGroup, int viewType) {
        View v = new View(viewGroup.getContext());
        return new ViewHolder(v);
    }

    class ViewHolder extends BaseViewHolder<DataObject> {
        public ViewHolder(@NotNull View itemView) {
            super(itemView);
        }
    }
    class ProgressViewHolder extends BaseViewHolder<DataObject> {
        public ProgressViewHolder(@NotNull View itemView) {
            super(itemView);
        }
    }

}
