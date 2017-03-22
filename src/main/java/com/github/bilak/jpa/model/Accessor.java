package com.github.bilak.jpa.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Created by lvasek on 16/10/2016.
 */
@Entity
@Table(name = "accessor")
public class Accessor {

	@Id
	@Column(name = "id", length = 36)
	private String id;
	@Column(name = "account_id", length = 36)
	private String accountId;
	@Column(name = "email")
	private String email;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "surname")
	private String surname;
	@Column(name = "user_password")
	private String password;
	@Type(type = "numeric_boolean")
	@Column(name = "locked")
	private Boolean locked;
	@Type(type = "numeric_boolean")
	@Column(name = "enabled")
	private Boolean enabled;

	@Column(name = "role", length = 20)
	private String role;

	public String getId() {
		return id;
	}

	public Accessor setId(String id) {
		this.id = id;
		return this;
	}

	public String getAccountId() {
		return accountId;
	}

	public Accessor setAccountId(String accountId) {
		this.accountId = accountId;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Accessor setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public Accessor setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public Accessor setSurname(String surname) {
		this.surname = surname;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public Accessor setPassword(String password) {
		this.password = password;
		return this;
	}

	public Boolean getLocked() {
		return locked;
	}

	public Accessor setLocked(Boolean locked) {
		this.locked = locked;
		return this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public Accessor setEnabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getRole() {
		return role;
	}

	public Accessor setRole(String role) {
		this.role = role;
		return this;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, accountId, email, firstName, surname, password, locked, enabled, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final Accessor other = (Accessor) obj;
		return Objects.equals(this.id, other.id)
				&& Objects.equals(this.accountId, other.accountId)
				&& Objects.equals(this.email, other.email)
				&& Objects.equals(this.firstName, other.firstName)
				&& Objects.equals(this.surname, other.surname)
				&& Objects.equals(this.password, other.password)
				&& Objects.equals(this.locked, other.locked)
				&& Objects.equals(this.enabled, other.enabled)
				&& Objects.equals(this.role, other.role);
	}

	@Override
	public String toString() {
		return "Accessor{" +
				"id='" + id + '\'' +
				", accountId='" + accountId + '\'' +
				", email='" + email + '\'' +
				", firstName='" + firstName + '\'' +
				", surname='" + surname + '\'' +
				", password='" + password + '\'' +
				", locked=" + locked +
				", enabled=" + enabled +
				", role='" + role + '\'' +
				'}';
	}
}
