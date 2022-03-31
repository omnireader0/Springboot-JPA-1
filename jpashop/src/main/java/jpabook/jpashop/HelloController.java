package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    // hello url로 오면 이 컨트롤러 호출
    // 컨트롤러에서 모델에 data 실어 뷰에 넘긴다 -> return "뷰네임"
    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("data", "hello!");
        return "hello";
    }
}