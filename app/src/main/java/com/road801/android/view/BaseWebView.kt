package com.road801.android.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.road801.android.databinding.FragmentBaseWebViewBinding

internal class BaseWebView : Fragment() {

    lateinit var binding: FragmentBaseWebViewBinding
    private val args: BaseWebViewArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBaseWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            view.findNavController().navigateUp()
        }

        binding.toolbar.title = args.title

        val settings: WebSettings = binding.webView.settings
        settings.builtInZoomControls = false
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(false)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        binding.webView.loadUrl(args.url)
    }


}