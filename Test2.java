package com.hfkh.xsb.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Scanner;

import com.hfkh.xsb.weibo.Util;
import com.hfkh.xsb.weibo.WeiBoUtil;

public class Test2 {
	public static void main(String[] args)  {
		
		
		try {
			Scanner input = new Scanner(new File("c://cookie.txt"));
			//System.out.println(input.nextLine());
			Test2.weibotest("hfkh/home", input.nextLine());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static void weibotest(String uid,String cookie){
		try {
			start("http://picupload.service.weibo.com/interface/pic_upload.php?app=miniblog&data=1&url=weibo.com/"+uid+"&markpos=1&logo=1&marks=1&mime=image/jpeg&ct=0.04204501071944833",
					cookie
					,"http://weibo.com/"+uid);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void start(String uploadUrl,String cookie,String referer) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException{
		Scanner input = new Scanner(new File("c://cookie1.txt"));
		String alimamaCookie = input.nextLine();
		String html = 
				Util.getHTML("http://pub.alimama.com/items/search.json?spm=a219t.7900221%2F1.1998910419.de727cf05.MvuJFX&toPage=1&queryType=2&perPageSize=40&dpyhq=1&auctionTag=&shopTag=dpyhq&t=1476880351768&_tb_token_=RQij7Dmr90q&pvid=10_120.210.165.152_2335_1476880223810", "utf-8", 
						alimamaCookie, null);

		List<String> auctionIdList = Util.getStringFromHTML(html, "\"auctionId\":(.*?),");
		List<String> couponAmountList = Util.getStringFromHTML(html, "\"couponAmount\":(.*?),");
		List<String> zkPriceList = Util.getStringFromHTML(html, "\"zkPrice\":(.*?),");
		List<String> titleList = Util.getStringFromHTML(html, "\"title\":\"(.*?)\"");
		List<String> picList = Util.getStringFromHTML(html, "\"pictUrl\":\"(.*?)\"");
		
		if(auctionIdList!=null){
			for(int i=0;i<auctionIdList.size();i++){
				String title = titleList.get(i);
				
				if(title==null) continue;
				
				title = title.replaceAll("<(.*?)>", "");
				
				String html2 = 
				Util.getHTML("http://pub.alimama.com/common/code/getAuctionCode.json?auctionid="+auctionIdList.get(i)
						+"&adzoneid=33730099&siteid=10124281&scenes=1&t=1476501365353&_tb_token_=y6Ibz24JKzp&pvid=10_36.5.102.98_366_1476501261899", "utf-8", 
						alimamaCookie, null);

				String couponLink = Util.getOne(html2, "\"couponLink\":\"(.*?)\"");
				String productLink = Util.getOne(html2, "\"shortLinkUrl\":\"(.*?)\"");
				
				String weiboText = title +"  "+productLink+"  价格:￥"+zkPriceList.get(i)
				+"  优惠券:￥"+couponAmountList.get(i)
						+"  "+couponLink;
			
				
				
				if(new Double(couponAmountList.get(i))>=20){
					String imgUrl = picList.get(i);
					String imgFilePath = "c://"+ imgUrl.substring(imgUrl.lastIndexOf("/")+1);
					Util.download(imgUrl,imgFilePath);
	
					WeiBoUtil.sendWeiBo(uploadUrl,imgFilePath, 
							weiboText,
							cookie,referer); 
	
					File file = new File(imgFilePath);
					file.delete();
					
					try {
						Thread.sleep(1000*60*1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}
}
