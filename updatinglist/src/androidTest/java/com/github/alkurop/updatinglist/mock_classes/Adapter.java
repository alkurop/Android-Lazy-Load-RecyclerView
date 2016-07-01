package com.github.alkurop.updatinglist.mock_classes;

import android.view.ViewGroup;
import com.github.alkurop.updatinglist.BaseLoadMoreAdapter;
import com.github.alkurop.updatinglist.BaseViewHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Created by alkurop on 01.07.16.
 */
public class Adapter extends BaseLoadMoreAdapter<DataObject> {
    @NotNull @Override public BaseViewHolder<DataObject> onCreateProgressVH(@NotNull ViewGroup viewGroup) {
        return null;
    }

    @NotNull @Override public BaseViewHolder<DataObject> onCreateVH(@NotNull ViewGroup viewGroup, int viewType) {
        return null;
    }
}
