package pl.edu.pjatk.tau.labone.service;

import java.util.List;
import pl.edu.pjatk.tau.labone.domain.Product;

public interface OrderService {

	List<Product> getRepository();

	void createProduct(Product p);
}