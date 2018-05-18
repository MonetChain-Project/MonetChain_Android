package com.lingtuan.firefly.contact.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingtuan.firefly.NextApplication;
import com.lingtuan.firefly.R;
import com.lingtuan.firefly.contact.vo.FriendRecommentVo;
import com.lingtuan.firefly.listener.RequestListener;
import com.lingtuan.firefly.util.MyToast;
import com.lingtuan.firefly.util.netutil.NetRequestImpl;

import org.json.JSONObject;

import java.util.List;

/**
 * Friend recommended adapter
 */
public class RecommentContactListAdapter extends BaseAdapter {

	private List<FriendRecommentVo> mList;
	private Context mContext;

	
	public RecommentContactListAdapter(List<FriendRecommentVo> mList, Context mContext) {
		this.mList = mList;
		this.mContext = mContext;
	}

	public void updateList(List<FriendRecommentVo> mList){
		this.mList = mList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder h;
		final FriendRecommentVo vo = mList.get(position);
		if(convertView == null){
			h = new Holder();
			convertView = View.inflate(mContext, R.layout.item_msg_contact, null);
			h.avatar = (ImageView) convertView.findViewById(R.id.item_avatar);
			h.content = (TextView) convertView.findViewById(R.id.item_content);
			h.time = (TextView) convertView.findViewById(R.id.item_times);
			h.nickname = (TextView) convertView.findViewById(R.id.item_nickname);
			h.agreeBtn = (TextView) convertView.findViewById(R.id.item_msg_event_agree);
			convertView.setTag(h);
		}else{
			h = (Holder) convertView.getTag();
		}
		
		String nickname = vo.getUsername();
		String content = mContext.getResources().getString(R.string.contact_friends);

		if(!TextUtils.isEmpty(vo.getThirdName())){
			content = content.concat("：" + vo.getThirdName());
		}
		h.time.setVisibility(View.GONE);
		NextApplication.displayCircleImage(h.avatar, vo.getThumb());
		h.nickname.setText(nickname);
		h.content.setText(content);
		
		h.agreeBtn.setVisibility(View.VISIBLE);
		
		if(vo.isAgree()){
			h.agreeBtn.setText(R.string.added);
			h.agreeBtn.setTextColor(mContext.getResources().getColor(R.color.textColorHint));
			h.agreeBtn.setEnabled(false);
			h.agreeBtn.setBackgroundDrawable(null);
		}else{
			h.agreeBtn.setEnabled(true);
			h.agreeBtn.setText(R.string.add_friends);
			h.agreeBtn.setTextColor(mContext.getResources().getColor(R.color.textColor));
			h.agreeBtn.setBackgroundResource(R.drawable.selector_round_black_5);
		}
		h.agreeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				requestAgree(vo);
				
			}
		});
		return convertView;
	}
	
	static class Holder {
		ImageView avatar;
		TextView time;
		TextView agreeBtn;
		TextView content;
		TextView nickname;
	}
	
	private void requestAgree(final FriendRecommentVo msg){
		NetRequestImpl.getInstance().addFriend(msg.getUid(),null,null, new RequestListener() {
			@Override
			public void start() {

			}

			@Override
			public void success(JSONObject response) {
				MyToast.showToast(mContext, response.optString("msg"));
				notifyDataSetChanged();
			}

			@Override
			public void error(int errorCode, String errorMsg) {
				MyToast.showToast(mContext, errorMsg);
				if (1211021 == errorCode) {//Have as a friend
					msg.setAgree(true);
					notifyDataSetChanged();
				}
			}
		});
	}
	
}
