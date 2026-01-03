package com.outdoor.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.outdoor.demo.entity.Equipment;
import com.outdoor.demo.entity.EquipmentChecklist;
import com.outdoor.demo.entity.EquipmentChecklistItem;
import com.outdoor.demo.entity.EquipmentRental;
import com.outdoor.demo.entity.EquipmentRentalRequest;
import com.outdoor.demo.entity.PersonalEquipment;
import com.outdoor.demo.service.EquipmentChecklistService;
import com.outdoor.demo.service.EquipmentRentalService;
import com.outdoor.demo.service.EquipmentService;
import com.outdoor.demo.service.PersonalEquipmentService;

@RestController
@RequestMapping("/api/equipment")
@Validated
/**
 * 装备控制器
 * 处理装备租赁、装备清单、个人装备管理等相关功能的HTTP请求
 */
public class EquipmentController {
    private final EquipmentService equipmentService;
    private final EquipmentRentalService rentalService;
    private final EquipmentChecklistService checklistService;
    private final PersonalEquipmentService personalEquipmentService;

    public EquipmentController(EquipmentService equipmentService, EquipmentRentalService rentalService, 
                              EquipmentChecklistService checklistService, PersonalEquipmentService personalEquipmentService) {
        this.equipmentService = equipmentService;
        this.rentalService = rentalService;
        this.checklistService = checklistService;
        this.personalEquipmentService = personalEquipmentService;
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) {
            throw new RuntimeException("未登录");
        }
        return (Long) uid;
    }

    // =========================== 装备租赁相关接口 ===========================

    /**
     * 获取所有可租赁装备
     */
    @GetMapping("/rental/list")
    public ResponseEntity<?> getAllRentalEquipments() {
        List<Equipment> equipments = equipmentService.getAllAvailableEquipments();
        Map<String, Object> body = new HashMap<>();
        body.put("equipments", equipments);
        return ResponseEntity.ok(body);
    }

    /**
     * 根据类型获取可租赁装备
     */
    @GetMapping("/rental/list/type/{type}")
    public ResponseEntity<?> getEquipmentsByType(@PathVariable String type) {
        List<Equipment> equipments = equipmentService.getEquipmentsByType(type);
        Map<String, Object> body = new HashMap<>();
        body.put("equipments", equipments);
        return ResponseEntity.ok(body);
    }

    /**
     * 获取装备详情
     */
    @GetMapping("/rental/detail/{id}")
    public ResponseEntity<?> getEquipmentDetail(@PathVariable Long id) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        if (equipment == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "装备不存在");
            return ResponseEntity.badRequest().body(body);
        }
        return ResponseEntity.ok(equipment);
    }

    /**
     * 创建装备租赁订单
     */
    @PostMapping("/rental/create")
    public ResponseEntity<?> createRentalOrder(@Valid @RequestBody EquipmentRentalRequest request, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            EquipmentRental rental = rentalService.createRental(request, userId);
            Map<String, Object> body = new HashMap<>();
            body.put("rental", rental);
            body.put("message", "租赁订单创建成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 获取用户的租赁订单列表
     */
    @GetMapping("/rental/my")
    public ResponseEntity<?> getMyRentalOrders(HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            List<EquipmentRental> rentals = rentalService.getRentalsByUserId(userId);
            Map<String, Object> body = new HashMap<>();
            body.put("rentals", rentals);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 完成支付
     */
    @PostMapping("/rental/payment/{id}")
    public ResponseEntity<?> completePayment(@PathVariable Long id) {
        boolean success = rentalService.completePayment(id);
        Map<String, Object> body = new HashMap<>();
        if (success) {
            body.put("message", "支付成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "支付失败");
            return ResponseEntity.badRequest().body(body);
        }
    }

    // =========================== 装备清单相关接口 ===========================

    /**
     * 创建智能装备清单
     */
    @PostMapping("/checklist/smart")
    public ResponseEntity<?> createSmartChecklist(@RequestParam String name, @RequestParam String routeType, 
                                                 @RequestParam String weather, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            EquipmentChecklist checklist = checklistService.createSmartChecklist(userId, name, routeType, weather);
            Map<String, Object> body = new HashMap<>();
            body.put("checklist", checklist);
            body.put("message", "智能装备清单创建成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 获取用户的装备清单列表
     */
    @GetMapping("/checklist/my")
    public ResponseEntity<?> getMyChecklists(HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            List<EquipmentChecklist> checklists = checklistService.getChecklistsByUserId(userId);
            Map<String, Object> body = new HashMap<>();
            body.put("checklists", checklists);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 获取清单详情
     */
    @GetMapping("/checklist/detail/{id}")
    public ResponseEntity<?> getChecklistDetail(@PathVariable Long id) {
        EquipmentChecklist checklist = checklistService.getChecklistById(id);
        if (checklist == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "清单不存在");
            return ResponseEntity.badRequest().body(body);
        }
        List<EquipmentChecklistItem> items = checklistService.getChecklistItems(id);
        Map<String, Object> body = new HashMap<>();
        body.put("checklist", checklist);
        body.put("items", items);
        return ResponseEntity.ok(body);
    }

    /**
     * 更新清单名称
     */
    @PutMapping("/checklist/name/{id}")
    public ResponseEntity<?> updateChecklistName(@PathVariable Long id, @RequestParam String name) {
        boolean success = checklistService.updateChecklistName(id, name);
        Map<String, Object> body = new HashMap<>();
        if (success) {
            body.put("message", "清单名称更新成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "清单不存在");
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 删除清单
     */
    @DeleteMapping("/checklist/{id}")
    public ResponseEntity<?> deleteChecklist(@PathVariable Long id) {
        boolean success = checklistService.deleteChecklist(id);
        Map<String, Object> body = new HashMap<>();
        if (success) {
            body.put("message", "清单删除成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "清单不存在");
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 添加清单项目
     */
    @PostMapping("/checklist/item")
    public ResponseEntity<?> addChecklistItem(@RequestParam Long checklistId, @RequestParam(required = false) Long equipmentId, 
                                             @RequestParam(required = false) String customName, @RequestParam Integer quantity) {
        try {
            EquipmentChecklistItem item = checklistService.addChecklistItem(checklistId, equipmentId, customName, quantity);
            Map<String, Object> body = new HashMap<>();
            body.put("item", item);
            body.put("message", "清单项目添加成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 更新项目打包状态
     */
    @PutMapping("/checklist/item/packed/{id}")
    public ResponseEntity<?> updateItemPackedStatus(@PathVariable Long id, @RequestParam Boolean isPacked) {
        boolean success = checklistService.updateItemPackedStatus(id, isPacked);
        Map<String, Object> body = new HashMap<>();
        if (success) {
            body.put("message", "打包状态更新成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "项目不存在");
            return ResponseEntity.badRequest().body(body);
        }
    }

    // =========================== 个人装备管理相关接口 ===========================

    /**
     * 添加个人装备
     */
    @PostMapping("/personal/add")
    public ResponseEntity<?> addPersonalEquipment(@Valid @RequestBody PersonalEquipmentAddRequest request, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            PersonalEquipment equipment = personalEquipmentService.addPersonalEquipment(
                    userId, request.getName(), request.getType(), request.getPurchaseDate(), request.getNote());
            Map<String, Object> body = new HashMap<>();
            body.put("equipment", equipment);
            body.put("message", "个人装备添加成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 获取个人装备列表
     */
    @GetMapping("/personal/list")
    public ResponseEntity<?> getPersonalEquipments(HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            List<PersonalEquipment> equipments = personalEquipmentService.getPersonalEquipmentsByUserId(userId);
            Map<String, Object> body = new HashMap<>();
            body.put("equipments", equipments);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 根据类型获取个人装备
     */
    @GetMapping("/personal/list/type/{type}")
    public ResponseEntity<?> getPersonalEquipmentsByType(@PathVariable String type, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            List<PersonalEquipment> equipments = personalEquipmentService.getPersonalEquipmentsByType(userId, type);
            Map<String, Object> body = new HashMap<>();
            body.put("equipments", equipments);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 更新装备使用次数
     */
    @PutMapping("/personal/usage/{id}")
    public ResponseEntity<?> updateUsageCount(@PathVariable Long id) {
        boolean success = personalEquipmentService.increaseUsageCount(id);
        Map<String, Object> body = new HashMap<>();
        if (success) {
            body.put("message", "使用次数更新成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "装备不存在");
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 获取需要维护的装备列表
     */
    @GetMapping("/personal/maintenance")
    public ResponseEntity<?> getEquipmentsNeedingMaintenance(HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            List<PersonalEquipment> equipments = personalEquipmentService.getEquipmentsNeedingMaintenance(userId);
            Map<String, Object> body = new HashMap<>();
            body.put("equipments", equipments);
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).body(body);
        }
    }

    /**
     * 删除个人装备
     */
    @DeleteMapping("/personal/delete/{id}")
    public ResponseEntity<?> deletePersonalEquipment(@PathVariable Long id) {
        boolean success = personalEquipmentService.deletePersonalEquipment(id);
        Map<String, Object> body = new HashMap<>();
        if (success) {
            body.put("message", "个人装备删除成功");
            return ResponseEntity.ok(body);
        } else {
            body.put("message", "装备不存在");
            return ResponseEntity.badRequest().body(body);
        }
    }

    /**
     * 个人装备添加请求实体
     */
    public static class PersonalEquipmentAddRequest {
        private String name;
        private String type;
        private String purchaseDate;
        private String note;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        body.put("message", errorMsg);
        return ResponseEntity.badRequest().body(body);
    }
}
