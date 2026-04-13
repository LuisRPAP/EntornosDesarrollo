<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>RenaceGest | Portal Principal</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<div class="app-shell">
	<section class="session-banner">
		<div>
			<strong>Sesion:</strong> ${currentUserName} (${currentRole})
		</div>
		<a class="button button-secondary" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
	</section>

	<header class="hero">
		<div>
			<div class="brand-lockup">
				<img class="brand-mark" src="${pageContext.request.contextPath}/assets/img/emblema-guardia.svg" alt="Emblema de la guardia">
				<p class="eyebrow">Guardias de Santiago</p>
			</div>
			<h1>RenaceGest</h1>
			<p class="lead">Gestion de alardes, grupos de mision, inventario historico y acceso cultural para Amigos de la Guardia.</p>
		</div>
		<div class="hero-actions">
			<c:if test="${canManageGuardias}">
				<a class="button button-primary" href="${pageContext.request.contextPath}/guardias">CRUD guardias</a>
			</c:if>
			<a class="button button-primary" href="${pageContext.request.contextPath}/grupos">${canManageGroups ? 'Gestionar grupos' : 'Consultar grupos'}</a>
			<a class="button button-secondary" href="${pageContext.request.contextPath}/mensajes">Mensajería</a>
			<a class="button button-primary" href="${pageContext.request.contextPath}/inventario">${canManageInventario ? 'Inventario y alardes' : 'Inventario (solo lectura)'}</a>
			<a class="button button-secondary" href="${pageContext.request.contextPath}/amigos">Acceso publico QR</a>
		</div>
	</header>

	<section class="stats-grid">
		<article class="stat-card">
			<span>Guardias</span>
			<strong>${totalGuardias}</strong>
		</article>
		<article class="stat-card">
			<span>Grupos</span>
			<strong>${totalGrupos}</strong>
		</article>
		<article class="stat-card">
			<span>Mensajes</span>
			<strong>${totalMensajes}</strong>
		</article>
		<article class="stat-card">
			<span>Pertrechos</span>
			<strong>${totalPertrechos}</strong>
		</article>
		<article class="stat-card">
			<span>Alardes</span>
			<strong>${totalAlardes}</strong>
		</article>
		<article class="stat-card">
			<span>Tickets maestranza</span>
			<strong>${totalTickets}</strong>
		</article>
	</section>

	<section class="panel-grid">
		<article class="panel">
			<div class="panel-head">
				<h2>Guardias activos</h2>
				<c:if test="${canManageGuardias}">
					<a href="${pageContext.request.contextPath}/guardias">Gestionar guardias</a>
				</c:if>
			</div>
			<div class="list">
				<c:forEach items="${guardias}" var="guardia">
					<div class="list-item">
						<div>
							<strong>${guardia.apodo}</strong>
							<p>${guardia.nombreReal} · ${guardia.rango}</p>
						</div>
						<span class="badge ${guardia.estadoHonor == 'Infame' ? 'badge-danger' : 'badge-ok'}">${guardia.estadoHonor}</span>
					</div>
				</c:forEach>
			</div>
		</article>

		<article class="panel">
			<div class="panel-head">
				<h2>Grupos y misiones</h2>
				<a href="${pageContext.request.contextPath}/mensajes">Abrir mensajería</a>
			</div>
			<div class="list">
				<c:forEach items="${grupos}" var="grupo">
					<div class="list-item">
						<div>
							<strong>${grupo.nombreGrupo}</strong>
							<p>${grupo.descripcion}</p>
						</div>
						<span class="badge badge-neutral">${grupo.tipo}</span>
					</div>
				</c:forEach>
			</div>
		</article>

		<article class="panel">
			<div class="panel-head">
				<h2>Inventario activo</h2>
				<a href="${pageContext.request.contextPath}/inventario">Gestionar alardes</a>
			</div>
			<div class="list">
				<c:forEach items="${pertrechos}" var="pertrecho">
					<div class="list-item">
						<div>
							<strong>${pertrecho.descripcion}</strong>
							<p>${pertrecho.seccionNombre} · Integridad ${pertrecho.integridad}%</p>
						</div>
						<span class="badge ${pertrecho.disponible ? 'badge-ok' : 'badge-danger'}">${pertrecho.disponible ? 'Disponible' : 'Prestado'}</span>
					</div>
				</c:forEach>
			</div>
		</article>
	</section>

	<section class="panel">
		<div class="panel-head">
			<h2>Mensajes recientes</h2>
			<a href="${pageContext.request.contextPath}/mensajes">Ver todos</a>
		</div>
		<div class="timeline">
			<c:forEach items="${mensajes}" var="mensaje">
				<article class="message-card">
					<div class="message-meta">
						<strong>${mensaje.emisorApodo}</strong>
						<span>${mensaje.fechaEnvio}</span>
					</div>
					<p>${mensaje.contenido}</p>
					<small>
						<c:choose>
							<c:when test="${mensaje.broadcast}">Broadcast global</c:when>
							<c:otherwise>Grupo: ${mensaje.grupoNombre}</c:otherwise>
						</c:choose>
					</small>
				</article>
			</c:forEach>
		</div>
	</section>
</div>
</body>
</html>
