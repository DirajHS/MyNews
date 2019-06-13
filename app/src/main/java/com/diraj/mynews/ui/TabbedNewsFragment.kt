package com.diraj.mynews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.diraj.mynews.R
import com.diraj.mynews.databinding.TabbedNewsLayoutBinding
import com.diraj.mynews.model.Articles

class TabbedNewsFragment : Fragment(), IOnNewsClickInterface {

    private lateinit var tabbedNewsLayoutBinding: TabbedNewsLayoutBinding

    private lateinit var categoryStrings: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tabbedNewsLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.tabbed_news_layout, container, false)
        categoryStrings = resources.getStringArray(R.array.categories)

        return tabbedNewsLayoutBinding.root
    }

    override fun onNewsArticleClick(article: Articles, view: View, transitionName: String) {

        val extras = FragmentNavigatorExtras(view to transitionName)
        val bundle = Bundle()
        bundle.putSerializable("article", article)
        Navigation.findNavController(this.activity!!, R.id.navigationHostFragment)
            .navigate(R.id.action_tabbedNewsFragment_to_newsDetailsFragment, bundle, null, extras)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TabbedNewsFragment::class.simpleName, "onResume")
        val categoryPagerAdapter = CategoryPagerAdapter(childFragmentManager)
        val viewPager = tabbedNewsLayoutBinding.pager
        viewPager.adapter = categoryPagerAdapter
        tabbedNewsLayoutBinding.tabLayout.setupWithViewPager(viewPager)
        (activity as IActionBarInterface).setUpActionBar()
    }

    inner class CategoryPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val topHeadlinesFragment = TopHeadlinesFragment.newInstance(categoryStrings[position])
            topHeadlinesFragment.setNewsClickListener(this@TabbedNewsFragment)
            return topHeadlinesFragment
        }

        override fun getCount(): Int {
            return categoryStrings.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return categoryStrings[position]
        }

    }

}
