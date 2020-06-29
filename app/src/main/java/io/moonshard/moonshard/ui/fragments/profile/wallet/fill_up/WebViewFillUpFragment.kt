package io.moonshard.moonshard.ui.fragments.profile.wallet.fill_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.view.profile.wallet.fill_up.WebViewFillUpView
import kotlinx.android.synthetic.main.fragment_web_view_fill_up.*
import moxy.MvpAppCompatFragment


class WebViewFillUpFragment : MvpAppCompatFragment(), WebViewFillUpView {

    private var url:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view_fill_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            url = it.getString("url")
        }

        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString());

                return true
            }
        }


    backBtn?.setSafeOnClickListener {
        parentFragmentManager.popBackStack()
          //  activity!!.onBackPressed()
     //       (activity!!.supportFragmentManager).popBackStack()
        }
    }
}