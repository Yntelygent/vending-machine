package pl.edu.pjatk.tau.labone.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pl.edu.pjatk.tau.labone.domain.Product;
import pl.edu.pjatk.tau.labone.exception.DuplicatedIdException;
import pl.edu.pjatk.tau.labone.exception.ProductNotFoundException;

public class OrderServiceImpl implements OrderService {

	private List<Product> repository = new ArrayList<>();

	@Override
	public List<Product> getRepository() {
		return this.repository;
	}

	@Override
	public void createProduct(Product p1) {
		for (Product p : repository) {
			if (p.getId() == p1.getId()) {
				throw new DuplicatedIdException();
			}
		}
		repository.add(p1);
	}

	@Override
	public List<Product> getAllProducts() {
		return repository;
	}

	@Override
	public Product getProductById(int id) {
		for (Product p : repository) {
			if (p.getId() == id) {
				return p;
			}
		}
		throw new ProductNotFoundException();
	}

	@Override
	public void updateProduct(Product p) {
		Product p1 = getProductById(p.getId());
		p1.setName(p.getName());
		p1.setPrice(p.getPrice());
	}

	@Override
	public void deleteProduct(int i) {
		for (Iterator<Product> iter = repository.listIterator(); iter.hasNext(); ) {
			Product p = iter.next();
			if (p.getId() == i) {
				iter.remove();
			}
		}
	}
}