package com.nandu.controller.admin;

import com.github.pagehelper.PageInfo;
import com.nandu.pojo.Blog;
import com.nandu.pojo.User;
import com.nandu.service.BlogService;
import com.nandu.service.TagService;
import com.nandu.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class BlogsController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    public String blogs(Model model,
                        @RequestParam(defaultValue = "1",value = "page")Integer page,
                        @RequestParam(defaultValue = "5",value = "size") Integer size) {
        Map<String,Object> map = new HashMap<>();
        map.put("title",null);
        map.put("typeId",null);
        map.put("recommend",null);
        PageInfo<Blog> pageInfo = blogService.findForPage(page, size, map);

        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("types",typeService.findallType());
        return "admin/blogs";
    }

    @PostMapping("/blogs/search")
    public String search(Model model,
                        @RequestParam(defaultValue = "1",value = "page")Integer page,
                        @RequestParam(defaultValue = "5",value = "size") Integer size,
                        String title,Long typeId,boolean recommend){
        Map<String,Object> map = new HashMap<>();
        map.put("title",title);
        map.put("typeId",typeId);
        map.put("recommend",recommend);
        System.out.println(recommend);
        model.addAttribute("pageInfo",blogService.findForPage(page,size,map));
        model.addAttribute("types",typeService.findallType());
        return "admin/blogs :: blogList";
    }

    @GetMapping("/blogs-input")
    public String toBlogInput(Model model){
        model.addAttribute("types",typeService.findallType());
        model.addAttribute("tags",tagService.findallTag());
        model.addAttribute("blog",new Blog());
        return "admin/blogs-input";
    }

    @GetMapping("/blogs/{id}/update")
    public String toBlogUpdate(@PathVariable Long id,Model model){
        model.addAttribute("blog",blogService.findbyId(id));
        model.addAttribute("types",typeService.findallType());
        model.addAttribute("tags",tagService.findallTag());
        return "admin/blogs-input";
    }

    @PostMapping("/blogs")
    public String FormHandler(Blog blog, Model model, HttpSession session, RedirectAttributes attributes){
        int res = 0;
        User user = (User) session.getAttribute("user");
        blog.setUserId(user.getId());
        //??????blog???type
        blog.setType(typeService.findTypebyId(blog.getType().getId()));
        //??????blog???typeId??????
        blog.setTypeId(blog.getType().getId());
        //???blog??????List<Tag>??????
        blog.setTags(tagService.findTagByString(blog.getTagIds()));
        if (blog.getId() == null) {   //?????????id??????????????????
            res = blogService.addBlog(blog);
        } else {
            blogService.delBlogAndTagbyBid(blog.getId());  //??????????????????????????????????????????,??????t_blogs_tags??????????????????
            res = blogService.updateBlog(blog);
        }
        if(res != 0){
            attributes.addFlashAttribute("message","???????????????");
        } else {
            attributes.addFlashAttribute("message","????????????????????????");
        }
        return "redirect:/admin/blogs";
    }


    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        if (blogService.delBlog(id) != 0){
            attributes.addFlashAttribute("message","???????????????");
        }else {
            attributes.addFlashAttribute("message","????????????");
        }
        return "redirect:/admin/blogs";
    }





}
