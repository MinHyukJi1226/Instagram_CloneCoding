package org.techtown.instagram_clonecoding.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.techtown.instagram_clonecoding.R
import org.techtown.instagram_clonecoding.navigation.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment() {
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String?= null
    var auth : FirebaseAuth ?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        fragmentView?.account_recyclerView?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerView?.layoutManager = GridLayoutManager(activity!!, 3)
        return fragmentView
    }
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        init {
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //Sometimes, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var imageview = (p0 as CustomViewHolder).imageview
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }
}