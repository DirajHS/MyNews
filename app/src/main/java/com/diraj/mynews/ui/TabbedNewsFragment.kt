package com.diraj.mynews.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.diraj.mynews.R
import com.diraj.mynews.databinding.TabbedNewsLayoutBinding

class TabbedNewsFragment : Fragment() {

    private lateinit var tabbedNewsLayoutBinding: TabbedNewsLayoutBinding

    private lateinit var categoryStrings : Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tabbedNewsLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.tabbed_news_layout, container, false)
        categoryStrings = resources.getStringArray(R.array.categories)

        return tabbedNewsLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryPagerAdapter = CategoryPagerAdapter(fragmentManager!!)
        val viewPager = tabbedNewsLayoutBinding.pager
        viewPager.adapter = categoryPagerAdapter
        tabbedNewsLayoutBinding.tabLayout.setupWithViewPager(viewPager)
    }

    inner class CategoryPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return TopHeadlinesFragment.newInstance(categoryStrings[position])
        }

        override fun getCount(): Int {
            return categoryStrings.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return categoryStrings[position]
        }

    }

}
