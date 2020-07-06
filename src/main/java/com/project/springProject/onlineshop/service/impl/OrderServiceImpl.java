package com.project.springProject.onlineshop.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.springProject.onlineshop.model.Order;
import com.project.springProject.onlineshop.model.OrderDetail;
import com.project.springProject.onlineshop.model.Product;
import com.project.springProject.onlineshop.model.dto.CartInfo;
import com.project.springProject.onlineshop.model.dto.CartLineInfo;
import com.project.springProject.onlineshop.model.dto.CustomerInfo;
import com.project.springProject.onlineshop.model.dto.OrderDetailInfo;
import com.project.springProject.onlineshop.model.dto.OrderInfo;
import com.project.springProject.onlineshop.pagination.PaginationResult;
import com.project.springProject.onlineshop.repository.OrderRepository;
import com.project.springProject.onlineshop.service.OrderService;
import com.project.springProject.onlineshop.service.ProductService;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
    private SessionFactory sessionFactory;
 
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderRepository orderRepository;
	
	@Transactional(rollbackFor = Exception.class)
    public void saveOrder(CartInfo cartInfo) {
        Session session = this.sessionFactory.getCurrentSession();
 
        int orderNum = this.getMaxOrderNum() + 1;
        Order order = new Order();
 
        order.setId(UUID.randomUUID().toString());
        order.setOrderNum(orderNum);
        order.setOrderDate(new Date());
        order.setAmount(cartInfo.getAmountTotal());
 
        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
        order.setCustomerName(customerInfo.getName());
        order.setCustomerEmail(customerInfo.getEmail());
        order.setCustomerPhone(customerInfo.getPhone());
        order.setCustomerAddress(customerInfo.getAddress());
 
        session.persist(order);
 
        List<CartLineInfo> lines = cartInfo.getCartLines();
 
        for (CartLineInfo line : lines) {
            OrderDetail detail = new OrderDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setOrder(order);
            detail.setAmount(line.getAmount());
            detail.setPrice(line.getProductInfo().getPrice());
            detail.setQuanity(line.getQuantity());
 
            String code = line.getProductInfo().getCode();
            Optional<Product> product = this.productService.findByCode(code);
            detail.setProduct(product.orElse(null));
 
            session.persist(detail);
        }
 
        // Order Number!
        cartInfo.setOrderNum(orderNum);
        // Flush
        session.flush();
    }
	
	private int getMaxOrderNum() {
        String sql = "Select max(o.orderNum) from " + Order.class.getName() + " o ";
        Session session = this.sessionFactory.getCurrentSession();
        Query<Integer> query = session.createQuery(sql, Integer.class);
        Integer value = (Integer) query.getSingleResult();
        if (value == null) {
            return 0;
        }
        return value;
    }

	@Override
	public PaginationResult<OrderInfo> listOrderInfo(int page, int maxResult, int maxNavigationPage) {
		// @page = 1, 2, ...
		String sql = "Select new " + OrderInfo.class.getName()//
                + "(ord.id, ord.orderDate, ord.orderNum, ord.amount, "
                + " ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) " + " from "
                + Order.class.getName() + " ord "//
                + " order by ord.orderNum desc";
//		String sql = "Select order.orderNum, orderDetail.product.code, orderDetail.amount, orderDetail.price, orderDetail.quanity " + 
//				"FROM OrderDetail as orderDetail inner join orderDetail.order as order;";
 
        Session session = this.sessionFactory.getCurrentSession();
        Query<OrderInfo> query = session.createQuery(sql, OrderInfo.class);
        return new PaginationResult<OrderInfo>(query, page, maxResult, maxNavigationPage);
	}
	
	@Override
	public OrderInfo getOrderInfo(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (!order.isPresent()) {
            return null;
        }
        return new OrderInfo(order.get().getId(), order.get().getOrderDate(), //
                order.get().getOrderNum(), order.get().getAmount(), order.get().getCustomerName(), //
                order.get().getCustomerAddress(), order.get().getCustomerEmail(), order.get().getCustomerPhone());
    }

	@Override
	public List<OrderDetailInfo> listOrderDetailInfos(String orderId) {
        String sql = "Select new " + OrderDetailInfo.class.getName() //
                + "(d.id, d.product.code, d.product.name , d.quanity,d.price,d.amount) "//
                + " from " + OrderDetail.class.getName() + " d "//
                + " where d.order.id = :orderId ";
 
        Session session = this.sessionFactory.getCurrentSession();
        Query<OrderDetailInfo> query = session.createQuery(sql, OrderDetailInfo.class);
        query.setParameter("orderId", orderId);
 
        return query.getResultList();
    }
}
