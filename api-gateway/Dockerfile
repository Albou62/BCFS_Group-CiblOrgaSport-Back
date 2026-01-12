FROM nginx:alpine

RUN rm /etc/nginx/nginx.conf
RUN rm /etc/nginx/conf.d/default.conf

COPY api-gateway/nginx.conf /etc/nginx/nginx.conf
COPY api-gateway/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]