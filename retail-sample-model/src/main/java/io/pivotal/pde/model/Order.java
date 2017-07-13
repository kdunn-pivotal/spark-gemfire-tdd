package io.pivotal.pde.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Order {
    BigInteger order_id;
    Integer customer_id;
    Integer store_id;
    String order_datetime;
    String ship_completion_datetime;
    String return_datetime;
    String refund_datetime;
    Integer payment_method_code;
    Float total_tax_amount;
    Float total_paid_amount;
    Float total_item_quantity;
    Float total_discount_amount;
    Integer coupon_code;
    Float coupon_amount;
    String order_canceled_flag;
    String has_returned_items_flag;
    String has_refunded_items_flag;
    Integer fraud_code;
    Integer fraud_resolution_code;
    String billing_address_line1;
    String billing_address_line2;
    String billing_address_line3;
    String billing_address_city;
    String billing_address_state;
    Integer billing_address_postal_code;
    String billing_address_country;
    String billing_phone_number;
    String customer_name;
    String customer_email_address;
    Integer ordering_session_id;
    String website_url;

    List<OrderLineItem> items;

    public Order() {
    }

    public Order(BigInteger order_id) {
        this.order_id = order_id;
    }

    public Order(BigInteger order_id, Float total_paid_amount, Float total_item_quantity) {
        this.order_id = order_id;
        this.total_paid_amount = total_paid_amount;
        this.total_item_quantity = total_item_quantity;
    }

    public Order(BigInteger order_id, Integer customer_id, Integer store_id, String order_datetime, String ship_completion_datetime, String return_datetime, String refund_datetime, Integer payment_method_code, Float total_tax_amount, Float total_paid_amount, Float total_item_quantity, Float total_discount_amount, Integer coupon_code, Float coupon_amount, String order_canceled_flag, String has_returned_items_flag, String has_refunded_items_flag, Integer fraud_code, Integer fraud_resolution_code, String billing_address_line1, String billing_address_line2, String billing_address_line3, String billing_address_city, String billing_address_state, Integer billing_address_postal_code, String billing_address_country, String billing_phone_number, String customer_name, String customer_email_address, Integer ordering_session_id, String website_url) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.store_id = store_id;
        this.order_datetime = order_datetime;
        this.ship_completion_datetime = ship_completion_datetime;
        this.return_datetime = return_datetime;
        this.refund_datetime = refund_datetime;
        this.payment_method_code = payment_method_code;
        this.total_tax_amount = total_tax_amount;
        this.total_paid_amount = total_paid_amount;
        this.total_item_quantity = total_item_quantity;
        this.total_discount_amount = total_discount_amount;
        this.coupon_code = coupon_code;
        this.coupon_amount = coupon_amount;
        this.order_canceled_flag = order_canceled_flag;
        this.has_returned_items_flag = has_returned_items_flag;
        this.has_refunded_items_flag = has_refunded_items_flag;
        this.fraud_code = fraud_code;
        this.fraud_resolution_code = fraud_resolution_code;
        this.billing_address_line1 = billing_address_line1;
        this.billing_address_line2 = billing_address_line2;
        this.billing_address_line3 = billing_address_line3;
        this.billing_address_city = billing_address_city;
        this.billing_address_state = billing_address_state;
        this.billing_address_postal_code = billing_address_postal_code;
        this.billing_address_country = billing_address_country;
        this.billing_phone_number = billing_phone_number;
        this.customer_name = customer_name;
        this.customer_email_address = customer_email_address;
        this.ordering_session_id = ordering_session_id;
        this.website_url = website_url;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id='" + order_id + '\'' +
                ", store_id='" + store_id + '\'' +
                ", order_datetime='" + order_datetime + '\'' +
                ", total_tax_amount='" + total_tax_amount + '\'' +
                ", total_paid_amount='" + total_paid_amount + '\'' +
                ", total_item_quantity='" + total_item_quantity + '\'' +
                ", total_discount_amount='" + total_discount_amount + '\'' +
                ", customer_name='" + customer_name + '\'' +
                ", customer_email_address='" + customer_email_address + '\'' +
                ", items=" + items +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (order_id != null ? !order_id.equals(order.order_id) : order.order_id != null) return false;
        if (customer_id != null ? !customer_id.equals(order.customer_id) : order.customer_id != null) return false;
        if (store_id != null ? !store_id.equals(order.store_id) : order.store_id != null) return false;
        if (order_datetime != null ? !order_datetime.equals(order.order_datetime) : order.order_datetime != null)
            return false;
        if (ship_completion_datetime != null ? !ship_completion_datetime.equals(order.ship_completion_datetime) : order.ship_completion_datetime != null)
            return false;
        if (return_datetime != null ? !return_datetime.equals(order.return_datetime) : order.return_datetime != null)
            return false;
        if (refund_datetime != null ? !refund_datetime.equals(order.refund_datetime) : order.refund_datetime != null)
            return false;
        if (payment_method_code != null ? !payment_method_code.equals(order.payment_method_code) : order.payment_method_code != null)
            return false;
        if (total_tax_amount != null ? !total_tax_amount.equals(order.total_tax_amount) : order.total_tax_amount != null)
            return false;
        if (total_paid_amount != null ? !total_paid_amount.equals(order.total_paid_amount) : order.total_paid_amount != null)
            return false;
        if (total_item_quantity != null ? !total_item_quantity.equals(order.total_item_quantity) : order.total_item_quantity != null)
            return false;
        if (total_discount_amount != null ? !total_discount_amount.equals(order.total_discount_amount) : order.total_discount_amount != null)
            return false;
        if (coupon_code != null ? !coupon_code.equals(order.coupon_code) : order.coupon_code != null) return false;
        if (coupon_amount != null ? !coupon_amount.equals(order.coupon_amount) : order.coupon_amount != null)
            return false;
        if (order_canceled_flag != null ? !order_canceled_flag.equals(order.order_canceled_flag) : order.order_canceled_flag != null)
            return false;
        if (has_returned_items_flag != null ? !has_returned_items_flag.equals(order.has_returned_items_flag) : order.has_returned_items_flag != null)
            return false;
        if (has_refunded_items_flag != null ? !has_refunded_items_flag.equals(order.has_refunded_items_flag) : order.has_refunded_items_flag != null)
            return false;
        if (fraud_code != null ? !fraud_code.equals(order.fraud_code) : order.fraud_code != null) return false;
        if (fraud_resolution_code != null ? !fraud_resolution_code.equals(order.fraud_resolution_code) : order.fraud_resolution_code != null)
            return false;
        if (billing_address_line1 != null ? !billing_address_line1.equals(order.billing_address_line1) : order.billing_address_line1 != null)
            return false;
        if (billing_address_line2 != null ? !billing_address_line2.equals(order.billing_address_line2) : order.billing_address_line2 != null)
            return false;
        if (billing_address_line3 != null ? !billing_address_line3.equals(order.billing_address_line3) : order.billing_address_line3 != null)
            return false;
        if (billing_address_city != null ? !billing_address_city.equals(order.billing_address_city) : order.billing_address_city != null)
            return false;
        if (billing_address_state != null ? !billing_address_state.equals(order.billing_address_state) : order.billing_address_state != null)
            return false;
        if (billing_address_postal_code != null ? !billing_address_postal_code.equals(order.billing_address_postal_code) : order.billing_address_postal_code != null)
            return false;
        if (billing_address_country != null ? !billing_address_country.equals(order.billing_address_country) : order.billing_address_country != null)
            return false;
        if (billing_phone_number != null ? !billing_phone_number.equals(order.billing_phone_number) : order.billing_phone_number != null)
            return false;
        if (customer_name != null ? !customer_name.equals(order.customer_name) : order.customer_name != null)
            return false;
        if (customer_email_address != null ? !customer_email_address.equals(order.customer_email_address) : order.customer_email_address != null)
            return false;
        if (ordering_session_id != null ? !ordering_session_id.equals(order.ordering_session_id) : order.ordering_session_id != null)
            return false;
        if (website_url != null ? !website_url.equals(order.website_url) : order.website_url != null) return false;
        return items != null ? items.equals(order.items) : order.items == null;
    }

    @Override
    public int hashCode() {
        int result = order_id != null ? order_id.hashCode() : 0;
        result = 31 * result + (customer_id != null ? customer_id.hashCode() : 0);
        result = 31 * result + (store_id != null ? store_id.hashCode() : 0);
        result = 31 * result + (order_datetime != null ? order_datetime.hashCode() : 0);
        result = 31 * result + (ship_completion_datetime != null ? ship_completion_datetime.hashCode() : 0);
        result = 31 * result + (return_datetime != null ? return_datetime.hashCode() : 0);
        result = 31 * result + (refund_datetime != null ? refund_datetime.hashCode() : 0);
        result = 31 * result + (payment_method_code != null ? payment_method_code.hashCode() : 0);
        result = 31 * result + (total_tax_amount != null ? total_tax_amount.hashCode() : 0);
        result = 31 * result + (total_paid_amount != null ? total_paid_amount.hashCode() : 0);
        result = 31 * result + (total_item_quantity != null ? total_item_quantity.hashCode() : 0);
        result = 31 * result + (total_discount_amount != null ? total_discount_amount.hashCode() : 0);
        result = 31 * result + (coupon_code != null ? coupon_code.hashCode() : 0);
        result = 31 * result + (coupon_amount != null ? coupon_amount.hashCode() : 0);
        result = 31 * result + (order_canceled_flag != null ? order_canceled_flag.hashCode() : 0);
        result = 31 * result + (has_returned_items_flag != null ? has_returned_items_flag.hashCode() : 0);
        result = 31 * result + (has_refunded_items_flag != null ? has_refunded_items_flag.hashCode() : 0);
        result = 31 * result + (fraud_code != null ? fraud_code.hashCode() : 0);
        result = 31 * result + (fraud_resolution_code != null ? fraud_resolution_code.hashCode() : 0);
        result = 31 * result + (billing_address_line1 != null ? billing_address_line1.hashCode() : 0);
        result = 31 * result + (billing_address_line2 != null ? billing_address_line2.hashCode() : 0);
        result = 31 * result + (billing_address_line3 != null ? billing_address_line3.hashCode() : 0);
        result = 31 * result + (billing_address_city != null ? billing_address_city.hashCode() : 0);
        result = 31 * result + (billing_address_state != null ? billing_address_state.hashCode() : 0);
        result = 31 * result + (billing_address_postal_code != null ? billing_address_postal_code.hashCode() : 0);
        result = 31 * result + (billing_address_country != null ? billing_address_country.hashCode() : 0);
        result = 31 * result + (billing_phone_number != null ? billing_phone_number.hashCode() : 0);
        result = 31 * result + (customer_name != null ? customer_name.hashCode() : 0);
        result = 31 * result + (customer_email_address != null ? customer_email_address.hashCode() : 0);
        result = 31 * result + (ordering_session_id != null ? ordering_session_id.hashCode() : 0);
        result = 31 * result + (website_url != null ? website_url.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    public BigInteger getOrder_id() {
        return order_id;
    }

    public void setOrder_id(BigInteger order_id) {
        this.order_id = order_id;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getOrder_datetime() {
        return order_datetime;
    }

    public void setOrder_datetime(String order_datetime) {
        this.order_datetime = order_datetime;
    }

    public String getShip_completion_datetime() {
        return ship_completion_datetime;
    }

    public void setShip_completion_datetime(String ship_completion_datetime) {
        this.ship_completion_datetime = ship_completion_datetime;
    }

    public String getReturn_datetime() {
        return return_datetime;
    }

    public void setReturn_datetime(String return_datetime) {
        this.return_datetime = return_datetime;
    }

    public String getRefund_datetime() {
        return refund_datetime;
    }

    public void setRefund_datetime(String refund_datetime) {
        this.refund_datetime = refund_datetime;
    }

    public Integer getPayment_method_code() {
        return payment_method_code;
    }

    public void setPayment_method_code(Integer payment_method_code) {
        this.payment_method_code = payment_method_code;
    }

    public Float getTotal_tax_amount() {
        return total_tax_amount;
    }

    public void setTotal_tax_amount(Float total_tax_amount) {
        this.total_tax_amount = total_tax_amount;
    }

    public Float getTotal_paid_amount() {
        return total_paid_amount;
    }

    public void setTotal_paid_amount(Float total_paid_amount) {
        this.total_paid_amount = total_paid_amount;
    }

    public Float getTotal_item_quantity() {
        return total_item_quantity;
    }

    public void setTotal_item_quantity(Float total_item_quantity) {
        this.total_item_quantity = total_item_quantity;
    }

    public Float getTotal_discount_amount() {
        return total_discount_amount;
    }

    public void setTotal_discount_amount(Float total_discount_amount) {
        this.total_discount_amount = total_discount_amount;
    }

    public Integer getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(Integer coupon_code) {
        this.coupon_code = coupon_code;
    }

    public Float getCoupon_amount() {
        return coupon_amount;
    }

    public void setCoupon_amount(Float coupon_amount) {
        this.coupon_amount = coupon_amount;
    }

    public String getOrder_canceled_flag() {
        return order_canceled_flag;
    }

    public void setOrder_canceled_flag(String order_canceled_flag) {
        this.order_canceled_flag = order_canceled_flag;
    }

    public String getHas_returned_items_flag() {
        return has_returned_items_flag;
    }

    public void setHas_returned_items_flag(String has_returned_items_flag) {
        this.has_returned_items_flag = has_returned_items_flag;
    }

    public String getHas_refunded_items_flag() {
        return has_refunded_items_flag;
    }

    public void setHas_refunded_items_flag(String has_refunded_items_flag) {
        this.has_refunded_items_flag = has_refunded_items_flag;
    }

    public Integer getFraud_code() {
        return fraud_code;
    }

    public void setFraud_code(Integer fraud_code) {
        this.fraud_code = fraud_code;
    }

    public Integer getFraud_resolution_code() {
        return fraud_resolution_code;
    }

    public void setFraud_resolution_code(Integer fraud_resolution_code) {
        this.fraud_resolution_code = fraud_resolution_code;
    }

    public String getBilling_address_line1() {
        return billing_address_line1;
    }

    public void setBilling_address_line1(String billing_address_line1) {
        this.billing_address_line1 = billing_address_line1;
    }

    public String getBilling_address_line2() {
        return billing_address_line2;
    }

    public void setBilling_address_line2(String billing_address_line2) {
        this.billing_address_line2 = billing_address_line2;
    }

    public String getBilling_address_line3() {
        return billing_address_line3;
    }

    public void setBilling_address_line3(String billing_address_line3) {
        this.billing_address_line3 = billing_address_line3;
    }

    public String getBilling_address_city() {
        return billing_address_city;
    }

    public void setBilling_address_city(String billing_address_city) {
        this.billing_address_city = billing_address_city;
    }

    public String getBilling_address_state() {
        return billing_address_state;
    }

    public void setBilling_address_state(String billing_address_state) {
        this.billing_address_state = billing_address_state;
    }

    public Integer getBilling_address_postal_code() {
        return billing_address_postal_code;
    }

    public void setBilling_address_postal_code(Integer billing_address_postal_code) {
        this.billing_address_postal_code = billing_address_postal_code;
    }

    public String getBilling_address_country() {
        return billing_address_country;
    }

    public void setBilling_address_country(String billing_address_country) {
        this.billing_address_country = billing_address_country;
    }

    public String getBilling_phone_number() {
        return billing_phone_number;
    }

    public void setBilling_phone_number(String billing_phone_number) {
        this.billing_phone_number = billing_phone_number;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email_address() {
        return customer_email_address;
    }

    public void setCustomer_email_address(String customer_email_address) {
        this.customer_email_address = customer_email_address;
    }

    public Integer getOrdering_session_id() {
        return ordering_session_id;
    }

    public void setOrdering_session_id(Integer ordering_session_id) {
        this.ordering_session_id = ordering_session_id;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public void setWebsite_url(String website_url) {
        this.website_url = website_url;
    }

    public List<OrderLineItem> getItems() {
        return items;
    }

    public void setItems(List<OrderLineItem> items) {
        this.items = items;
    }

    public void addItem(OrderLineItem item) {
        if (this.items == null) {
            this.items = new ArrayList<OrderLineItem>();
        }

        this.items.add(item);
    }
}
