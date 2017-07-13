package io.pivotal.pde.model;

public class OrderLineItem {
    Integer order_id;
    Integer order_item_id;
    Integer product_id;
    String product_name;
    Integer customer_id;
    Integer store_id;
    Integer item_shipment_status_code;
    String order_datetime;
    String ship_datetime;
    String item_return_datetime;
    String item_refund_datetime;
    Integer product_category_id;
    String product_category_name;
    Integer payment_method_code;
    Float tax_amount;
    Float item_quantity;
    Float item_price;
    Float discount_amount;
    Integer coupon_code;
    Float coupon_amount;
    String ship_address_line1;
    String ship_address_line2;
    String ship_address_line3;
    String ship_address_city;
    String ship_address_state;
    Integer ship_address_postal_code;
    String ship_address_country;
    String ship_phone_number;
    String ship_customer_name;
    String ship_customer_email_address;
    Integer ordering_session_id;
    String website_url;

    public OrderLineItem() {
    }

    public OrderLineItem(Integer order_id, Integer order_item_id, String product_name, Float item_quantity, Float item_price) {
        this.order_id = order_id;
        this.order_item_id = order_item_id;
        this.product_name = product_name;
        this.item_quantity = item_quantity;
        this.item_price = item_price;
    }

    public OrderLineItem(Integer order_id, Integer order_item_id, Integer product_id, String product_name, Integer customer_id, Integer store_id, Integer item_shipment_status_code, String order_datetime, String ship_datetime, String item_return_datetime, String item_refund_datetime, Integer product_category_id, String product_category_name, Integer payment_method_code, Float tax_amount, Float item_quantity, Float item_price, Float discount_amount, Integer coupon_code, Float coupon_amount, String ship_address_line1, String ship_address_line2, String ship_address_line3, String ship_address_city, String ship_address_state, Integer ship_address_postal_code, String ship_address_country, String ship_phone_number, String ship_customer_name, String ship_customer_email_address, Integer ordering_session_id, String website_url) {
        this.order_id = order_id;
        this.order_item_id = order_item_id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.customer_id = customer_id;
        this.store_id = store_id;
        this.item_shipment_status_code = item_shipment_status_code;
        this.order_datetime = order_datetime;
        this.ship_datetime = ship_datetime;
        this.item_return_datetime = item_return_datetime;
        this.item_refund_datetime = item_refund_datetime;
        this.product_category_id = product_category_id;
        this.product_category_name = product_category_name;
        this.payment_method_code = payment_method_code;
        this.tax_amount = tax_amount;
        this.item_quantity = item_quantity;
        this.item_price = item_price;
        this.discount_amount = discount_amount;
        this.coupon_code = coupon_code;
        this.coupon_amount = coupon_amount;
        this.ship_address_line1 = ship_address_line1;
        this.ship_address_line2 = ship_address_line2;
        this.ship_address_line3 = ship_address_line3;
        this.ship_address_city = ship_address_city;
        this.ship_address_state = ship_address_state;
        this.ship_address_postal_code = ship_address_postal_code;
        this.ship_address_country = ship_address_country;
        this.ship_phone_number = ship_phone_number;
        this.ship_customer_name = ship_customer_name;
        this.ship_customer_email_address = ship_customer_email_address;
        this.ordering_session_id = ordering_session_id;
        this.website_url = website_url;
    }

    @Override
    public String toString() {
        return "OrderLineItem{" +
                "order_id='" + order_id + '\'' +
                ", order_item_id='" + order_item_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", store_id='" + store_id + '\'' +
                ", order_datetime='" + order_datetime + '\'' +
                ", product_category_name='" + product_category_name + '\'' +
                ", tax_amount='" + tax_amount + '\'' +
                ", item_quantity='" + item_quantity + '\'' +
                ", item_price='" + item_price + '\'' +
                ", discount_amount='" + discount_amount + '\'' +
                ", ordering_session_id='" + ordering_session_id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderLineItem that = (OrderLineItem) o;

        if (order_id != null ? !order_id.equals(that.order_id) : that.order_id != null) return false;
        if (order_item_id != null ? !order_item_id.equals(that.order_item_id) : that.order_item_id != null)
            return false;
        if (product_id != null ? !product_id.equals(that.product_id) : that.product_id != null) return false;
        if (product_name != null ? !product_name.equals(that.product_name) : that.product_name != null) return false;
        if (customer_id != null ? !customer_id.equals(that.customer_id) : that.customer_id != null) return false;
        if (store_id != null ? !store_id.equals(that.store_id) : that.store_id != null) return false;
        if (item_shipment_status_code != null ? !item_shipment_status_code.equals(that.item_shipment_status_code) : that.item_shipment_status_code != null)
            return false;
        if (order_datetime != null ? !order_datetime.equals(that.order_datetime) : that.order_datetime != null)
            return false;
        if (ship_datetime != null ? !ship_datetime.equals(that.ship_datetime) : that.ship_datetime != null)
            return false;
        if (item_return_datetime != null ? !item_return_datetime.equals(that.item_return_datetime) : that.item_return_datetime != null)
            return false;
        if (item_refund_datetime != null ? !item_refund_datetime.equals(that.item_refund_datetime) : that.item_refund_datetime != null)
            return false;
        if (product_category_id != null ? !product_category_id.equals(that.product_category_id) : that.product_category_id != null)
            return false;
        if (product_category_name != null ? !product_category_name.equals(that.product_category_name) : that.product_category_name != null)
            return false;
        if (payment_method_code != null ? !payment_method_code.equals(that.payment_method_code) : that.payment_method_code != null)
            return false;
        if (tax_amount != null ? !tax_amount.equals(that.tax_amount) : that.tax_amount != null) return false;
        if (item_quantity != null ? !item_quantity.equals(that.item_quantity) : that.item_quantity != null)
            return false;
        if (item_price != null ? !item_price.equals(that.item_price) : that.item_price != null) return false;
        if (discount_amount != null ? !discount_amount.equals(that.discount_amount) : that.discount_amount != null)
            return false;
        if (coupon_code != null ? !coupon_code.equals(that.coupon_code) : that.coupon_code != null) return false;
        if (coupon_amount != null ? !coupon_amount.equals(that.coupon_amount) : that.coupon_amount != null)
            return false;
        if (ship_address_line1 != null ? !ship_address_line1.equals(that.ship_address_line1) : that.ship_address_line1 != null)
            return false;
        if (ship_address_line2 != null ? !ship_address_line2.equals(that.ship_address_line2) : that.ship_address_line2 != null)
            return false;
        if (ship_address_line3 != null ? !ship_address_line3.equals(that.ship_address_line3) : that.ship_address_line3 != null)
            return false;
        if (ship_address_city != null ? !ship_address_city.equals(that.ship_address_city) : that.ship_address_city != null)
            return false;
        if (ship_address_state != null ? !ship_address_state.equals(that.ship_address_state) : that.ship_address_state != null)
            return false;
        if (ship_address_postal_code != null ? !ship_address_postal_code.equals(that.ship_address_postal_code) : that.ship_address_postal_code != null)
            return false;
        if (ship_address_country != null ? !ship_address_country.equals(that.ship_address_country) : that.ship_address_country != null)
            return false;
        if (ship_phone_number != null ? !ship_phone_number.equals(that.ship_phone_number) : that.ship_phone_number != null)
            return false;
        if (ship_customer_name != null ? !ship_customer_name.equals(that.ship_customer_name) : that.ship_customer_name != null)
            return false;
        if (ship_customer_email_address != null ? !ship_customer_email_address.equals(that.ship_customer_email_address) : that.ship_customer_email_address != null)
            return false;
        if (ordering_session_id != null ? !ordering_session_id.equals(that.ordering_session_id) : that.ordering_session_id != null)
            return false;
        return website_url != null ? website_url.equals(that.website_url) : that.website_url == null;
    }

    @Override
    public int hashCode() {
        int result = order_id != null ? order_id.hashCode() : 0;
        result = 31 * result + (order_item_id != null ? order_item_id.hashCode() : 0);
        result = 31 * result + (product_id != null ? product_id.hashCode() : 0);
        result = 31 * result + (product_name != null ? product_name.hashCode() : 0);
        result = 31 * result + (customer_id != null ? customer_id.hashCode() : 0);
        result = 31 * result + (store_id != null ? store_id.hashCode() : 0);
        result = 31 * result + (item_shipment_status_code != null ? item_shipment_status_code.hashCode() : 0);
        result = 31 * result + (order_datetime != null ? order_datetime.hashCode() : 0);
        result = 31 * result + (ship_datetime != null ? ship_datetime.hashCode() : 0);
        result = 31 * result + (item_return_datetime != null ? item_return_datetime.hashCode() : 0);
        result = 31 * result + (item_refund_datetime != null ? item_refund_datetime.hashCode() : 0);
        result = 31 * result + (product_category_id != null ? product_category_id.hashCode() : 0);
        result = 31 * result + (product_category_name != null ? product_category_name.hashCode() : 0);
        result = 31 * result + (payment_method_code != null ? payment_method_code.hashCode() : 0);
        result = 31 * result + (tax_amount != null ? tax_amount.hashCode() : 0);
        result = 31 * result + (item_quantity != null ? item_quantity.hashCode() : 0);
        result = 31 * result + (item_price != null ? item_price.hashCode() : 0);
        result = 31 * result + (discount_amount != null ? discount_amount.hashCode() : 0);
        result = 31 * result + (coupon_code != null ? coupon_code.hashCode() : 0);
        result = 31 * result + (coupon_amount != null ? coupon_amount.hashCode() : 0);
        result = 31 * result + (ship_address_line1 != null ? ship_address_line1.hashCode() : 0);
        result = 31 * result + (ship_address_line2 != null ? ship_address_line2.hashCode() : 0);
        result = 31 * result + (ship_address_line3 != null ? ship_address_line3.hashCode() : 0);
        result = 31 * result + (ship_address_city != null ? ship_address_city.hashCode() : 0);
        result = 31 * result + (ship_address_state != null ? ship_address_state.hashCode() : 0);
        result = 31 * result + (ship_address_postal_code != null ? ship_address_postal_code.hashCode() : 0);
        result = 31 * result + (ship_address_country != null ? ship_address_country.hashCode() : 0);
        result = 31 * result + (ship_phone_number != null ? ship_phone_number.hashCode() : 0);
        result = 31 * result + (ship_customer_name != null ? ship_customer_name.hashCode() : 0);
        result = 31 * result + (ship_customer_email_address != null ? ship_customer_email_address.hashCode() : 0);
        result = 31 * result + (ordering_session_id != null ? ordering_session_id.hashCode() : 0);
        result = 31 * result + (website_url != null ? website_url.hashCode() : 0);
        return result;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(Integer order_item_id) {
        this.order_item_id = order_item_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
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

    public Integer getItem_shipment_status_code() {
        return item_shipment_status_code;
    }

    public void setItem_shipment_status_code(Integer item_shipment_status_code) {
        this.item_shipment_status_code = item_shipment_status_code;
    }

    public String getOrder_datetime() {
        return order_datetime;
    }

    public void setOrder_datetime(String order_datetime) {
        this.order_datetime = order_datetime;
    }

    public String getShip_datetime() {
        return ship_datetime;
    }

    public void setShip_datetime(String ship_datetime) {
        this.ship_datetime = ship_datetime;
    }

    public String getItem_return_datetime() {
        return item_return_datetime;
    }

    public void setItem_return_datetime(String item_return_datetime) {
        this.item_return_datetime = item_return_datetime;
    }

    public String getItem_refund_datetime() {
        return item_refund_datetime;
    }

    public void setItem_refund_datetime(String item_refund_datetime) {
        this.item_refund_datetime = item_refund_datetime;
    }

    public Integer getProduct_category_id() {
        return product_category_id;
    }

    public void setProduct_category_id(Integer product_category_id) {
        this.product_category_id = product_category_id;
    }

    public String getProduct_category_name() {
        return product_category_name;
    }

    public void setProduct_category_name(String product_category_name) {
        this.product_category_name = product_category_name;
    }

    public Integer getPayment_method_code() {
        return payment_method_code;
    }

    public void setPayment_method_code(Integer payment_method_code) {
        this.payment_method_code = payment_method_code;
    }

    public Float getTax_amount() {
        return tax_amount;
    }

    public void setTax_amount(Float tax_amount) {
        this.tax_amount = tax_amount;
    }

    public Float getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(Float item_quantity) {
        this.item_quantity = item_quantity;
    }

    public Float getItem_price() {
        return item_price;
    }

    public void setItem_price(Float item_price) {
        this.item_price = item_price;
    }

    public Float getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Float discount_amount) {
        this.discount_amount = discount_amount;
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

    public String getShip_address_line1() {
        return ship_address_line1;
    }

    public void setShip_address_line1(String ship_address_line1) {
        this.ship_address_line1 = ship_address_line1;
    }

    public String getShip_address_line2() {
        return ship_address_line2;
    }

    public void setShip_address_line2(String ship_address_line2) {
        this.ship_address_line2 = ship_address_line2;
    }

    public String getShip_address_line3() {
        return ship_address_line3;
    }

    public void setShip_address_line3(String ship_address_line3) {
        this.ship_address_line3 = ship_address_line3;
    }

    public String getShip_address_city() {
        return ship_address_city;
    }

    public void setShip_address_city(String ship_address_city) {
        this.ship_address_city = ship_address_city;
    }

    public String getShip_address_state() {
        return ship_address_state;
    }

    public void setShip_address_state(String ship_address_state) {
        this.ship_address_state = ship_address_state;
    }

    public Integer getShip_address_postal_code() {
        return ship_address_postal_code;
    }

    public void setShip_address_postal_code(Integer ship_address_postal_code) {
        this.ship_address_postal_code = ship_address_postal_code;
    }

    public String getShip_address_country() {
        return ship_address_country;
    }

    public void setShip_address_country(String ship_address_country) {
        this.ship_address_country = ship_address_country;
    }

    public String getShip_phone_number() {
        return ship_phone_number;
    }

    public void setShip_phone_number(String ship_phone_number) {
        this.ship_phone_number = ship_phone_number;
    }

    public String getShip_customer_name() {
        return ship_customer_name;
    }

    public void setShip_customer_name(String ship_customer_name) {
        this.ship_customer_name = ship_customer_name;
    }

    public String getShip_customer_email_address() {
        return ship_customer_email_address;
    }

    public void setShip_customer_email_address(String ship_customer_email_address) {
        this.ship_customer_email_address = ship_customer_email_address;
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
}
