package com.pigeonligh.facade.ui.view.content;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pigeonligh.facade.Activities.ViewActivity;
import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.CachedImageGetter;
import com.pigeonligh.facade.data.types.ResponseView;
import com.pigeonligh.facade.databinding.FragmentViewContentBinding;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.LinkResolver;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImageItem;
import io.noties.markwon.image.ImageSizeResolverDef;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.SchemeHandler;

public class ViewContentFragment extends Fragment {
    private static final String TAG = "view.content";

    private FragmentViewContentBinding binding;
    private TextView contentView;
    private ViewActivity parentActivity;
    private Markwon markwon;

    private MarkwonPlugin viewPlugin;

    public ViewContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = (ViewActivity) getActivity();

        binding = FragmentViewContentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        contentView = root.findViewById(R.id.view_content_view);

        initMarkdown();

        return root;
    }

    private void initMarkdown() {
        viewPlugin = new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver(new LinkResolver() {
                    @Override
                    public void resolve(@NonNull View view, @NonNull String link) {
                        Log.d(TAG, String.format("link %s", link));
                    }
                });

                ImageSizeResolverDef def = new ImageSizeResolverDef();
                builder.imageSizeResolver(def);

                super.configureConfiguration(builder);
            }

            @Override
            public void configure(@NonNull Registry registry) {
                registry.require(ImagesPlugin.class, new Action<ImagesPlugin>() {
                    @Override
                    public void apply(@NonNull ImagesPlugin imagesPlugin) {
                        imagesPlugin.addSchemeHandler(new SchemeHandler() {
                            private final CachedImageGetter getter = new CachedImageGetter();

                            @NonNull
                            @Override
                            public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                                CachedImageGetter.ImageCache cache = getter.get(parentActivity.getMediaURL(raw));
                                return ImageItem.withDecodingNeeded(cache.contentType, new ByteArrayInputStream(cache.data));
                            }

                            @NonNull
                            @Override
                            public Collection<String> supportedSchemes() {
                                return Arrays.asList("media");
                            }
                        });
                    }
                });

                super.configure(registry);
            }
        };

        markwon = Markwon.builder(parentActivity.getApplicationContext()).
                usePlugin(HtmlPlugin.create()).
                usePlugin(ImagesPlugin.create()).
                usePlugin(viewPlugin).
                build();
    }

    @Override
    public void onResume() {
        ViewActivity.ViewState viewState = parentActivity.getCurrentView();
        ResponseView data = new ResponseView(viewState.getData());

        parentActivity.setTitle(data.content.title);
        markwon.setMarkdown(contentView, data.content.content);

        super.onResume();
    }


}