package com.springboot.testapp6.controller;

import com.springboot.testapp6.config.DataSourceConfig;
import com.springboot.testapp6.config.DynamicDataSource;
import com.springboot.testapp6.domain.User;
import com.springboot.testapp6.dto.ResultCheckUser;
import com.springboot.testapp6.dto.ResultCheckUsers;
import com.springboot.testapp6.form.TestForm;
import com.springboot.testapp6.service.TestService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

/** Test 애플리케이션 컨트롤러 */
@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    /** ID 대상 */
    @Autowired
    TestService service;

    /** form-baking bean 초기화 */
    @ModelAttribute
    public TestForm setUpForm() {
        TestForm form = new TestForm();
        form.setIsLogin(false);
        return form;
    }

    /**
     * 데이터 목록 표시
     */
    @GetMapping
    public String showList(TestForm form, Model model, HttpSession session) throws SQLException {
//        setUpForm();
        //목록 취득
        ResultCheckUsers result = service.selectAll();
        if (!result.isCheck()) {
            model.addAttribute("errmgs", result.getMessage());
        }

        //표시용 모델에 저장
        model.addAttribute("list", result.getUsers());
        model.addAttribute("dbList", DataSourceConfig.getDataSourceKeyList());
        model.addAttribute("selectedDb", DynamicDataSource.getNowKey());
        return "crud";
    }

    /**
     * 데이터를 1건 등록
     */
    @PostMapping("/insert")
    public String insert(@Validated TestForm form, BindingResult bindingResult, Model model, HttpSession session, RedirectAttributes redirectAttributes) throws Exception {

        // Form 에서 Entiry 로 넣기
        User data = make(form);

        //입력 체크
        if (!bindingResult.hasErrors()) {
            try {
                ResultCheckUser result = service.insert(data);
                if (result.isCheck()) {
                    redirectAttributes.addFlashAttribute("complete", "등록이 완료되었습니다.");
                } else {
                    redirectAttributes.addFlashAttribute("complete", result.getMessage());
                }
                return "redirect:/test";
            }
            catch (Exception e) {
                log.error("🚨 insertUser 중 오류 발생: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("complete", "등록에 실패했습니다.");
                return "redirect:/test";
            }

        } else {
            // 에러가 발생한 경우에는 목록 표시로 변경
            return showList(form, model, session);
        }
    }

    @PostMapping("/check")
    public String check(@Validated TestForm form, RedirectAttributes redirectAttributes) throws Exception {
        StringBuilder txt = new StringBuilder();
        try {
            ResultCheckUser result = service.checkAccountPassword(form.getUid(), form.getPassword());
            boolean c = result.isCheck();
            txt.append("decode pw:" + result.getUser().getPassword() + "<br/>");
            txt.append("decode txt:" + result.getUser().getTesttext()+ "<br/>");
            if (c) {
                txt.append("암호가 맞습니다.");
                redirectAttributes.addFlashAttribute("checkcomplete", txt);
                return "redirect:/test";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("checkcomplete", "암호 확인에 실패했습니다.");
            return "redirect:/test";
        }
        txt.append("암호가 틀렸습니다.");
        redirectAttributes.addFlashAttribute("checkcomplete", txt);
        return "redirect:/test";
    }

    /**
     * 데이터를 1건 취득해서 폼 안에 표시
     */
    @GetMapping("/{id}")
    public String showUpdate(TestForm form, @PathVariable Integer id, Model model) {
        // 데이터를 취득(Optional로 래핑)
        User data = service.selectById(id);
        form = makeForm(data);

        // 변경용 모델 생성
        makeUpdateModel(form,model);

        return "crud";
    }

    /**
     * 변경용 모델 생성
     */
    private void makeUpdateModel(TestForm form, Model model) {
        model.addAttribute("id", form.getId());
        model.addAttribute("testForm", form);
        model.addAttribute("title", "변경 폼");
    }

    // --- 아래는 Form과 도메인 객체를 다시 채우기 ---
    /**
     * form에서 data로 다시 채우기, 반환값으로 돌려줌
     */
    private User make(TestForm form) {
        User data = new User();
        data.setId(form.getId());
        data.setPassword(form.getPassword());
        data.setUserid(form.getUid());
        return data;
    }

    /**
     * data에서 form으로 다시 채우기, 반환값으로 돌려줌
     */
    private TestForm makeForm(User data) {
        TestForm form = new TestForm();
        form.setId(data.getId());
        form.setUid(data.getUserid());
//        form.setPassword(data.getValue());
        form.setIsLogin(true);
        return form;
    }

    /**
     * id를 키로 사용해 데이터를 삭제
     */
    @PostMapping("/delete")
    public String delete(@RequestParam("id") String id, Model model, RedirectAttributes redirectAttributes) throws Exception {
        // 데이터를 1건 삭제하고 리다이렉트
        service.delete(Integer.parseInt(id));
        redirectAttributes.addFlashAttribute("delcomplete", "삭제 완료했습니다");
        return "redirect:/test";
    }

    @GetMapping("/selectDb")
    public String setDb(@RequestParam("db") String db, HttpSession session, RedirectAttributes redirectAttributes) throws Exception {
        try {
            setUpForm();
            session.setAttribute("selectedDb", db);
            redirectAttributes.addFlashAttribute("changedDBcomplete", db + "로 변경 되었습니다.");
            return "redirect:/test";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("changedDBcomplete", db + " 변경 실패");
        }
        return "redirect:/test";
    }
}
