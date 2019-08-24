package com.example.restaurant.item

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restaurant.R
import com.example.restaurant.common.presentationLayer.BaseActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_details.*
import javax.inject.Inject


class ItemDetailsActivity : BaseActivity() {

    companion object {
        private const val ITEM_ID_KEY = "item_id"

        fun start(context: AppCompatActivity, itemId: Int, sharedImageView: View) {
            val intent = Intent(context, ItemDetailsActivity::class.java)
            intent.putExtra(ITEM_ID_KEY, itemId)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context,
                sharedImageView,
                context.getString(R.string.MenuItemImageViewSharedTransitionKey)
            )
            context.startActivity(intent, options.toBundle())
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var itemDetailsViewModel: ItemDetailsViewModel

    override fun getContentResource(): Int = R.layout.activity_item_details

    override fun init(state: Bundle?) {
        initToolbar()
        initViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    //TODO: Make this function abstract in parent
    private fun initViewModel() {
        itemDetailsViewModel = ViewModelProvider(this, viewModelFactory).get(ItemDetailsViewModel::class.java)
        itemDetailsViewModel.liveData.observe(this, Observer {
            render(it)
        })
        itemDetailsViewModel.init(getItemId())
    }

    private fun getItemId(): Int {
        if (!intent.hasExtra(ITEM_ID_KEY))
            throw IllegalAccessException("Intent must contain $ITEM_ID_KEY")
        return intent.getIntExtra(ITEM_ID_KEY, 0)
    }

    private fun render(itemDetailsState: ItemDetailsState) {
        itemDetailsState.item?.let {
            with(it) {
                collapsing_toolbar.title = name
                Picasso.get().load(photoUrl).placeholder(R.drawable.ic_placeholder).into(iv_item_photo)
                tv_item_description.text = description
            }
        }
    }

}
