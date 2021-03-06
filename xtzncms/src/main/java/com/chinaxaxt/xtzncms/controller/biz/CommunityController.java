package com.chinaxaxt.xtzncms.controller.biz;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chinaxaxt.xtzncms.common.vo.PageVO;
import com.chinaxaxt.xtzncms.common.vo.ResponseVO;
import com.chinaxaxt.xtzncms.entity.biz.Community;
import com.chinaxaxt.xtzncms.service.biz.CommunityService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 
 * @author 吴佳涛
 * 2018-6-29
 */
@Controller
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    CommunityService communityService;

    /**
     * 根据Id查询
     * @param uid
     * @return
     */
    @RequestMapping(value="/{uid}",method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getCommunity(@PathVariable("uid") String uid){
        Community community=new Community();
        if(uid!=null&&!"".equals(uid)) {
        	community.setUid(uid);
        }
        return new ResponseVO(800, "success",communityService.select(community));
    }
    
    /**
     * 查询所有
     * @return
     */
    @RequestMapping(value="/list",method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getCommunitys(){
        return new ResponseVO(800, "success",communityService.select(new Community()));
    }

    /**
     * 分页查询和关键字查询
     * @param page
     * @param limit
     * @param searchType
     * @param searchValue
     * @return
     */
    @RequestMapping(value="/page",method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getPolices(Integer page,Integer limit,String searchType,String searchValue){
    	String name = "name";
    	String synopsis = "synopsis";
    	Page<Object> pa = PageHelper.startPage(page, limit, "create_time");
    	PageVO pg = new PageVO();
		if("".equals(searchType) || "".equals(searchValue)) {
			pg.setData(communityService.select(new Community()));
		}else if(name.equals(searchType)) {
			pg.setData(communityService.selectCommunityByName(searchValue));
		}else if(synopsis.equals(searchType)) {
			pg.setData(communityService.selectCommunityBySynopsis(searchValue));
		}
    	pg.setNowPage(pa.getPageNum());
    	pg.setTotal(pa.getTotal());
        return ResponseVO.success( pg);
    }

    /**
     * 添加
     * @param files
     * @param name
     * @param synopsis
     * @param content
     * @param virtue
     * @param url
     * @return
     */
    @RequestMapping(value="",method = RequestMethod.POST)
    @ResponseBody
    public ResponseVO insertCompanyNews(@RequestParam("file[]") MultipartFile[] files,String name,String synopsis,String content,String imageName,String url){
    	List<String> list=communityService.multiUpdate(files);
		StringBuffer sbr=new StringBuffer();
		if(list.size()!=0){
			for (String string : list) {
				sbr.append(string+",");
			}
			sbr.substring(0, sbr.length()-1);
		}
    	Community cy = new Community();
    	cy.setName(name);
    	cy.setSynopsis(synopsis);
    	cy.setContent(content);
    	cy.setImage(sbr.toString());
    	cy.setImageName(imageName);
    	cy.setUrl(url);
    	try {
			this.communityService.insert(cy);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseVO(809,"解析文件出错");
		}
        return ResponseVO.success(null);
    }

//    @RequestMapping(value="",method = RequestMethod.PUT)
//    @ResponseBody
//    public ResponseVO updateCommunity(@RequestBody Community community){
//        return new ResponseVO(800, "success",communityService.update(community));
//    }


    /**
     * 根据id删除
     * @param uid
     * @return
     */
    @RequestMapping(value="",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseVO deleteByUid(String uid){
    	String[] url = communityService.selectCommunityByUid(uid).getImage().split(",");
    	for (int i = 0; i < url.length; i++) {
			String string = url[i];
			File file =new File("/usr/java/xtzncms/docs/images/"+string);
	    	file.delete();
		}
        return new ResponseVO(800, "success",communityService.deleteByUid(uid));
    }
}
